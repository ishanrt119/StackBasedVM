import java.io.*;
import java.util.*;

public class EnhancedStackVM {
    private Stack<Integer> stack = new Stack<>();
    private Map<String, Integer> labels = new HashMap<>();
    private int programCounter = 0;
    private List<String> program = new ArrayList<>();

    public EnhancedStackVM(List<String> program) {
        this.program = program;
        parseLabels();
    }

    private void parseLabels() {
        for (int i = 0; i < program.size(); i++) {
            String[] parts = program.get(i).split(" ");
            if (parts[0].endsWith(":")) {
                labels.put(parts[0].replace(":", ""), i);
            }
        }
    }

    public void run() {
        while (programCounter < program.size()) {
            String[] instruction = program.get(programCounter).split(" ");
            executeInstruction(instruction);
            programCounter++;
        }
    }

    private void executeInstruction(String[] instruction) {
        try {
            switch (instruction[0]) {
                case "PUSH":
                    stack.push(Integer.parseInt(instruction[1]));
                    break;
                case "POP":
                    checkUnderflow();
                    stack.pop();
                    break;
                case "DUP":
                    checkUnderflow();
                    stack.push(stack.peek());
                    break;
                case "SWAP":
                    checkUnderflow(2);
                    int a = stack.pop();
                    int b = stack.pop();
                    stack.push(a);
                    stack.push(b);
                    break;
                case "ADD":
                    checkUnderflow(2);
                    stack.push(stack.pop() + stack.pop());
                    break;
                case "SUB":
                    checkUnderflow(2);
                    stack.push(-stack.pop() + stack.pop());
                    break;
                case "MUL":
                    checkUnderflow(2);
                    stack.push(stack.pop() * stack.pop());
                    break;
                case "DIV":
                    checkUnderflow(2);
                    int divisor = stack.pop();
                    if (divisor == 0) throw new ArithmeticException("Division by zero");
                    stack.push(stack.pop() / divisor);
                    break;
                case "MOD":
                    checkUnderflow(2);
                    stack.push(stack.pop() % stack.pop());
                    break;
                case "CMPE": // Equals
                    checkUnderflow(2);
                    stack.push(stack.pop().equals(stack.pop()) ? 1 : 0);
                    break;
                case "CMPG": // Greater than
                    checkUnderflow(2);
                    stack.push(stack.pop() < stack.pop() ? 1 : 0);
                    break;
                case "CMPL": // Less than
                    checkUnderflow(2);
                    stack.push(stack.pop() > stack.pop() ? 1 : 0);
                    break;
                case "JMP":
                    programCounter = labels.getOrDefault(instruction[1], programCounter);
                    break;
                case "CJMP": // Conditional Jump
                    checkUnderflow();
                    if (stack.pop() == 1) {
                        programCounter = labels.getOrDefault(instruction[1], programCounter);
                    }
                    break;
                case "PRINT":
                    System.out.println("Top of stack: " + (stack.isEmpty() ? "Empty" : stack.peek()));
                    break;
                default:
                    if (instruction[0].endsWith(":")) { /* Label, no action required */ }
                    else { System.out.println("Unknown instruction: " + instruction[0]); }
                    break;
            }
        } catch (Exception e) {
            System.out.println("Error executing instruction " + Arrays.toString(instruction) + ": " + e.getMessage());
        }
    }

    private void checkUnderflow() {
        if (stack.isEmpty()) throw new EmptyStackException();
    }

    private void checkUnderflow(int n) {
        if (stack.size() < n) throw new EmptyStackException();
    }

    public static List<String> loadProgramFromFile(String filename) throws IOException {
        List<String> program = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                program.add(line);
            }
        }
        return program;
    }

    public static void saveProgramToFile(List<String> program, String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (String line : program) {
                writer.write(line);
                writer.newLine();
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        List<String> program = new ArrayList<>();

        System.out.println("Enter instructions for the VM (type 'END' to finish):");
        while (true) {
            String line = scanner.nextLine().trim();
            if (line.equalsIgnoreCase("END")) {
                break;
            }
            program.add(line);
        }

        System.out.print("Save program to file (yes/no)? ");
        if (scanner.nextLine().equalsIgnoreCase("yes")) {
            System.out.print("Enter filename: ");
            String filename = scanner.nextLine();
            try {
                saveProgramToFile(program, filename);
                System.out.println("Program saved to " + filename);
            } catch (IOException e) {
                System.out.println("Error saving program: " + e.getMessage());
            }
        }

        EnhancedStackVM vm = new EnhancedStackVM(program);
        vm.run();

        scanner.close();
    }
}
