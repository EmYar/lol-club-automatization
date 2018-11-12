package com.company;

import com.company.tasks.TaskType;
import one.util.streamex.StreamEx;
import uk.org.lidalia.sysoutslf4j.context.SysOutOverSLF4J;

import java.util.Set;

public class Main {

    public static void main(String[] args) {
        SysOutOverSLF4J.sendSystemOutAndErrToSLF4J();
        Set<String> arguments = Set.of(args);
        StreamEx.of(TaskType.values())
                .filter(taskType -> arguments.contains(taskType.getCommand()))
                .forEachOrdered(TaskType::runTask);
    }
}
