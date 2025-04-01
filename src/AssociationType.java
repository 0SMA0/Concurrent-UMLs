public enum AssociationType {
    INHERITANCE,      // "extends" or "implements"
    AGGREGATION,      // "has-a" weak relationship
    COMPOSITION,      // "has-a" strong relationship
    DEPENDENCY        // "uses-a" temporary relationship
}