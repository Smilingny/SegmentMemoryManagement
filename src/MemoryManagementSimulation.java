import java.util.*;

class Segment {
    private int segmentNumber; // 段号
    private int base;          // 起始地址
    private int limit;         // 段长度

    public Segment(int segmentNumber, int base, int limit) {
        this.segmentNumber = segmentNumber;
        this.base = base;
        this.limit = limit;
    }

    public int getSegmentNumber() {
        return segmentNumber;
    }

    public int getBase() {
        return base;
    }

    public int getLimit() {
        return limit;
    }

    public void setBase(int base) {
        this.base = base;
    }

    public void setLimit(int limit) {
        this.limit = limit;
    }

    public void setSegmentNumber(int segmentNumber) {
        this.segmentNumber = segmentNumber;
    }
}

class Process {
    private int processId; // 进程ID
    private int size;

    public Process(int processId, int size) {
        this.processId = processId;
        this.size = size;
    }

    public int getProcessId() {
        return processId;
    }

    public int getSize() {
        return size;
    }
}

public class MemoryManagementSimulation {
    private static final int MEMORY_SIZE = 100; // 内存大小

    private List<Segment> memory; // 内存
    private List<Process> processes; // 用户进程

    public MemoryManagementSimulation() {
        memory = new ArrayList<>();
        processes = new ArrayList<>();
    }

    public void allocateSegment(Process process) {
        int base = 0;
        boolean allocated = false;

        // 首次适应算法
        for (Segment segment : memory) {
            if (segment.getLimit() >= process.getSize() && segment.getSegmentNumber() == 0) {
                int remainingSpace = segment.getLimit() - process.getSize();

                if (remainingSpace > 0) {
                    // 分配段
                    Segment allocatedSegment = new Segment(process.getProcessId(), segment.getBase(), process.getSize());
                    segment.setBase(segment.getBase() + process.getSize());
                    segment.setLimit(remainingSpace);
                    memory.add(memory.indexOf(segment), allocatedSegment);
                } else {
                    // 完全使用段
                    segment.setSegmentNumber(process.getProcessId());
                }

                allocated = true;
                break;
            }

            base = segment.getBase() + segment.getLimit();
        }

        // 分配失败时，将进程加入内存末尾
        if (!allocated) {
            Segment newSegment = new Segment(process.getProcessId(), base, process.getSize());
            memory.add(newSegment);
        }
    }

    public void deallocateSegment(int processId) {
        Segment segmentToRemove = null;

        // 查找要释放的段
        for (Segment segment : memory) {
            if (segment.getSegmentNumber() == processId) {
                segmentToRemove = segment;
                break;
            }
        }

        // 释放段
        if (segmentToRemove != null) {
            segmentToRemove.setSegmentNumber(0);
            mergeFreeSegments();
        }
    }

    private void mergeFreeSegments() {
        for (int i = 0; i < memory.size() - 1; i++) {
            Segment currentSegment = memory.get(i);
            Segment nextSegment = memory.get(i + 1);

            if (currentSegment.getSegmentNumber() == 0 && nextSegment.getSegmentNumber() == 0) {
                currentSegment.setLimit(currentSegment.getLimit() + nextSegment.getLimit());
                memory.remove(nextSegment);
                i--; // 重新检查当前位置
            }
        }
    }

    public void displayMemory() {
        System.out.println("Memory Status:");
        System.out.println("----------------------");

        for (Segment segment : memory) {
            System.out.println("Segment Number: " + segment.getSegmentNumber());
            System.out.println("Base: " + segment.getBase());
            System.out.println("Limit: " + segment.getLimit());
            System.out.println("----------------------");
        }
    }

    public static void main(String[] args) {
        MemoryManagementSimulation simulation = new MemoryManagementSimulation();

        // 添加用户进程
        Process process1 = new Process(1, 20);
        Process process2 = new Process(2, 15);
        Process process3 = new Process(3, 30);

        simulation.allocateSegment(process1);
        simulation.allocateSegment(process2);
        simulation.allocateSegment(process3);

        simulation.displayMemory();

        // 释放进程
        simulation.deallocateSegment(2);

        simulation.displayMemory();
    }
}


