public abstract class Shape {
    private final String name;

    protected Shape(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("name must not be blank");
        }
        this.name = name;
    }

    public final String getName() {
        return name;
    }

    // Abstraction: every Shape must know how to compute its area.
    public abstract double area();

    @Override
    public String toString() {
        return name;
    }
}

