import path from 'path';
import * as vscode from 'vscode';
import * as FileSystem from 'fs';
import { spawn } from 'child_process';

export function activate(context: vscode.ExtensionContext) {
    registerCommands(context);
}

async function openPlantUMLPreview(uri: vscode.Uri) {
    const content = FileSystem.readFileSync(uri.fsPath, 'utf-8');



    console.log(content);
}


function registerCommands(context: vscode.ExtensionContext) {
    const generateFromFileCommand = vscode.commands.registerCommand('umlGenerator.generateFromFile', async (uri: vscode.Uri) => {
        try {
            await generateUMLFromFile(uri);
        } catch (error) {
            vscode.window.showErrorMessage(`Error generating UML from file: ${error}`);
        }
    });

    const generateFromFolderCommand = vscode.commands.registerCommand('umlGenerator.generateFromFolder', async(uri: vscode.Uri) => {
        try {
            await generateUMLFromFolder(uri);
        } catch (error) {
            vscode.window.showErrorMessage(`Error generating UML from folder: ${error}`);
        }
    });

    const openSettingsCommand = vscode.commands.registerCommand('umlGenerator.openSettings', () => {
        vscode.commands.executeCommand('workbench.action.openSettings', 'umlGenerator');
    });

    const openPlantUMLPreviewCommand = vscode.commands.registerCommand('umlGenerator.openWebView', async (uri: vscode.Uri) => {
        try {
            await openPlantUMLPreview(uri);
        } catch (error) {

        }
    })

    context.subscriptions.push(
        generateFromFileCommand,
        generateFromFolderCommand,
        openSettingsCommand,
        openPlantUMLPreviewCommand
    );
}

async function generateUMLFromFile(uri: vscode.Uri) {
    const config = vscode.workspace.getConfiguration('umlGenerator');
    const includeRelationships = config.get<boolean>('includeRelationships', true);
    const verbose = config.get<boolean>('verbose', false);
    const autoOpenFile = config.get<boolean>('autoOpenFile', true);

    // Fixed: isFolder should be false for single files
    await generateUMLFromPath(uri.fsPath, false, {
        includeRelationships,
        verbose,
        autoOpenFile
    });
}

async function generateUMLFromFolder(uri: vscode.Uri) {
    const config = vscode.workspace.getConfiguration('umlGenerator');
    const includeRelationships = config.get<boolean>('includeRelationships', true);
    const verbose = config.get<boolean>('verbose', false);
    const autoOpenFile = config.get<boolean>('autoOpenFile', true);

    // Correct: isFolder should be true for folders
    await generateUMLFromPath(uri.fsPath, true, {
        includeRelationships,
        verbose,
        autoOpenFile
    });
}

interface UMLGeneratorOptions {
    includeRelationships: boolean;
    verbose: boolean;
    autoOpenFile: boolean;
}

async function generateUMLFromPath(inputPath: string, isFolder: boolean, options: UMLGeneratorOptions) {
    return new Promise<void>((resolve, reject) => {
        vscode.window.withProgress(
            {
                location: vscode.ProgressLocation.Notification,
                title: "Generating UML Diagram",
                cancellable: false
            }, async (progress) => {
                try {
                    progress.report({ increment: 0, message: "Starting UML generation..." });
                    const jarPath = await getJarPath();
                    if (!jarPath) {
                        throw new Error('UML Generator Jar not found');
                    }
                    
                    let outputDir: string;
                    let outputFile: string;
                    let inputName: string;

                    const config = vscode.workspace.getConfiguration('umlGenerator');
                    const customOutputDir = config.get<string>('outputDirectory');

                    if (isFolder) {
                        if (customOutputDir) {
                            const workspaceRoot = vscode.workspace.workspaceFolders?.[0]?.uri.fsPath;
                            outputDir = workspaceRoot ? path.join(workspaceRoot, customOutputDir) : path.join(inputPath, customOutputDir);
                        } else {
                            outputDir = inputPath; // Original behavior
                        }
                        inputName = path.basename(inputPath);
                        outputFile = path.join(outputDir, `${inputName}.puml`);
                    } else {
                        if (customOutputDir) {
                            const workspaceRoot = vscode.workspace.workspaceFolders?.[0]?.uri.fsPath;
                            outputDir = workspaceRoot ? path.join(workspaceRoot, customOutputDir) : path.join(path.dirname(inputPath), customOutputDir);
                        } else {
                            outputDir = path.dirname(inputPath); // Original behavior
                        }
                        inputName = path.basename(inputPath, '.java'); 
                        outputFile = path.join(outputDir, `${inputName}.puml`);
                    }
                    
                    if(!FileSystem.existsSync(outputDir)) {
                        FileSystem.mkdirSync(outputDir, {recursive: true});
                    }
                    
                    const args = ['-jar', jarPath, '-i', inputPath, '-o', outputFile];
                    if (!options.includeRelationships) {
                        args.push('--no-relationship');
                    }
                    if (options.verbose) {
                        args.push('-v');
                    }

                    // Debug logging
                    console.log(`Running command: java ${args.join(' ')}`);
                    console.log(`Expected output file: ${outputFile}`);

                    progress.report({increment: 25, message:"Running UML generator..."});

                    const javaProcess = spawn('java', args, {
                        cwd: path.dirname(jarPath)
                    });

                    let output = '';
                    let errorOutput = '';

                    javaProcess.stdout?.on('data', (data) => {
                        output += data.toString();
                        if (options.verbose) {
                            console.log(`stdout: ${data}`);
                        }
                    });

                    javaProcess.stderr?.on('data', (data) => {
                        errorOutput += data.toString();
                        if (options.verbose) {
                            console.error(`stderr: ${data}`);
                        }
                    });

                    javaProcess.on('close', async (code) => {
                        try {
                            progress.report({increment: 75, message: "Processing results..."});
                            
                            console.log(`Java process exited with code: ${code}`);
                            console.log(`stdout: ${output}`);
                            console.log(`stderr: ${errorOutput}`);
                            console.log(`Output file exists: ${FileSystem.existsSync(outputFile)}`);

                            if (code !== 0) {
                                throw new Error(`Java process failed with code ${code}. Error: ${errorOutput || 'No error output'}`);
                            }

                            if (!FileSystem.existsSync(outputFile)) {
                                throw new Error(`Output file was not created: ${outputFile}`);
                            }

                            const fileStats = FileSystem.statSync(outputFile);
                            if (fileStats.size === 0) {
                                throw new Error(`Output file is empty: ${outputFile}`);
                            }

                            const generatedContent = FileSystem.readFileSync(outputFile, 'utf8');
                            if (!generatedContent.trim()) {
                                throw new Error(`Output file contains no content: ${outputFile}`);
                            }

                            if (!generatedContent.includes('@startuml') || !generatedContent.includes('@enduml')) {
                                throw new Error(`Output file does not contain valid PlantUML content: ${outputFile}`);
                            }

                            // Success!
                            const message = `UML diagram generated successfully: ${outputFile}`;

                            vscode.window.showInformationMessage(message, 'Open File', 'Open Folder').then(selection => {
                                if (selection === "Open File") {
                                    vscode.commands.executeCommand('vscode.open', vscode.Uri.file(outputFile));
                                }
                                else if (selection === "Open Folder") {
                                    vscode.commands.executeCommand('revealFileInOS', vscode.Uri.file(outputFile));
                                }
                            });
                            
                            if (options.autoOpenFile) {
                                await vscode.commands.executeCommand('vscode.open', vscode.Uri.file(outputFile));
                            }
                            
                            progress.report({increment: 100, message: "Done!"});
                            resolve();                            
                        } catch (error) {
                            reject(error);
                        }
                    });
                    
                    javaProcess.on('error', (error) => {
                        reject(new Error(`Failed to start java process: ${error.message}`));
                    });

                } catch (error) {
                    reject(error);
                }
            }
        );
    });
}

async function getJarPath(): Promise<string | null> {
    const config = vscode.workspace.getConfiguration('umlGenerator');
    const customJarPath = config.get<string>('jarPath');
    
    // Fixed: Check custom JAR path first
    if (customJarPath && FileSystem.existsSync(customJarPath)) {
        return customJarPath;
    }
    
    // Check bundled JAR
    const extensionPath = vscode.extensions.getExtension('your-publisher-name.uml-generator')?.extensionPath;
    if (extensionPath) {
        const bundledJarPath = path.join(extensionPath, 'resources', 'uml-generator.jar');
        if (FileSystem.existsSync(bundledJarPath)) {
            return bundledJarPath;
        }
    }
    
    // Check common paths
    const commonPaths = [
        './uml-generator.jar',
        '../v2/target/uml-generator.jar',
        './v2/target/uml-generator.jar'
    ];

    for (const jarPath of commonPaths) {
        const resolvedPath = path.resolve(jarPath);
        if (FileSystem.existsSync(resolvedPath)) {
            return resolvedPath;
        }
    }

    // Handle JAR not found
    const result = await vscode.window.showErrorMessage(
        'UML Generator Jar not found. Please configure the JAR path in settings.',
        'Open Settings',
        'Browse for Jar'
    );

    if (result === 'Open Settings') {
        vscode.commands.executeCommand('workbench.action.openSettings', 'umlGenerator.jarPath');
    } else if (result === 'Browse for Jar') {
        const jarUri = await vscode.window.showOpenDialog({
            canSelectFiles: true,
            canSelectFolders: false,
            canSelectMany: false,
            filters: {
                'JAR Files': ['jar']
            }
        });
        if (jarUri && jarUri[0]) {
            await config.update('jarPath', jarUri[0].fsPath, vscode.ConfigurationTarget.Global);
            return jarUri[0].fsPath;
        }
    }
    return null;
}

export function deactivate() {
    console.log('UML Generator extension is now deactivated');
}