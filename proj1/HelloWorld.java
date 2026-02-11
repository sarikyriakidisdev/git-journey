public class HelloWorld {
    public static void main(String[] args) {
        System.out.println("=== Encapsulation + Composition ===");
        User youngUser = new User("ssar", 27);
        Book book = new Book("The Great Gatsby", "F. Scott Fitzgerald");

        youngUser.borrow(book);

        System.out.println(youngUser.getName() + " (" + youngUser.getAge() + ") borrowed: " + book);
        System.out.println("Total borrowed: " + youngUser.getBorrowedCount());

        // youngUser.age = 999; // won't compile: age is private (encapsulation)

        youngUser.haveBirthday();
        System.out.println("After birthday, age is: " + youngUser.getAge());

        System.out.println();
        System.out.println("=== Abstraction + Inheritance + Polymorphism ===");

        // Polymorphism: the variable type is Shape, but the object can be Circle/Rectangle.
        Shape[] shapes = new Shape[] {
                new Circle(2.0),
                new Rectangle(3.0, 4.0)
        };

        // Polymorphism: calling area() on Shape executes the overridden method of the real object.
        for (Shape s : shapes) {
            System.out.printf("%s area = %.2f%n", s.getName(), s.area());
        }
    }
}
