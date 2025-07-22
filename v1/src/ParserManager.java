import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.*;

public class ParserManager {
    private final BlockingQueue<String> fileQueue;
    private final ExecutorService executor;
    private final PlantUmlGenerator generator;
    private final CountDownLatch latch;
    private volatile boolean isProcessing = true;

    public ParserManager(int threadCount) {
        this.fileQueue = new LinkedBlockingQueue<>();
        this.executor = Executors.newFixedThreadPool(threadCount);
        this.generator = new PlantUmlGenerator(new UMLModel());
        this.latch = new CountDownLatch(threadCount);
    }

    public void submitTask(String filePath) {
        fileQueue.offer(filePath);
    }

    public void startProcessing() {
        for (int i = 0; i < ((ThreadPoolExecutor) executor).getCorePoolSize(); i++) {
            executor.submit(() -> {
                try {
                    while (isProcessing || !fileQueue.isEmpty()) {
                        String filePath = fileQueue.poll(1, TimeUnit.SECONDS);
                        if (filePath != null) {
                            processFile(new File(filePath));
                        }
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown();
                }
            });
        }
    }

    private void processFile(File file) {
        try {
            UMLModel model = new UMLModel();
            FileParser parser = new FileParser(file.getAbsolutePath(), model);
            parser.run();

            synchronized (generator) {
                generator.setUML(model);
                generator.genSingleClass();
            }
        } catch (Exception e) {
            System.err.println("Error processing file " + file.getName() + ": " + e.getMessage());
        }
    }

    public void shutdown() throws InterruptedException {
        isProcessing = false;
        latch.await(30, TimeUnit.SECONDS);
        executor.shutdown();
        if (!executor.awaitTermination(10, TimeUnit.SECONDS)) {
            executor.shutdownNow();
        }

        String finalUML = generator.putItAllTogether().toString();

        // Write to file
        try (PrintWriter out = new PrintWriter("output.puml", StandardCharsets.UTF_8)) {
            out.println(finalUML);
        } catch (IOException e) {
            System.err.println("Failed to write UML to file: " + e.getMessage());
        }

        // Encode for PlantUML editor
        String encoded = PlantUmlEncoder.encode(finalUML);
        String url = "https://www.plantuml.com/plantuml/png/" + encoded;

        System.out.println("View your UML here:\n" + url);
    }

    public static void main(String[] args) throws InterruptedException {
        ParserManager manager = new ParserManager(4);
        manager.startProcessing();

        manager.submitTask("TestingFiles//Animal.java");
        manager.submitTask("TestingFiles//Cat.java");
        manager.submitTask("TestingFiles//Dog.java");
        // manager.submitTask("TestingFiles//TestFile.java");

        manager.shutdown();
    }
}
