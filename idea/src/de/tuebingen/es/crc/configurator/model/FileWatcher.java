package de.tuebingen.es.crc.configurator.model;

import java.io.File;
import java.nio.file.*;
import java.util.List;

/**
 * Created by luebeck on 4/19/17.
 */
public class FileWatcher implements Runnable {

    File file;

    public FileWatcher(File file) {
        this.file = file;
    }

    @Override
    public void run() {
        Path dirOfFile = this.file.getParentFile().toPath();

        try {
            WatchService watchService = dirOfFile.getFileSystem().newWatchService();
            dirOfFile.register(watchService, StandardWatchEventKinds.ENTRY_CREATE, StandardWatchEventKinds.ENTRY_MODIFY, StandardWatchEventKinds.ENTRY_DELETE);

            WatchKey watchKey = watchService.take();

            boolean stop = false;

            while(!stop) {
                List<WatchEvent<?>> events = watchKey.pollEvents();

                for (WatchEvent event : events) {
                    String context = event.context().toString();
                    if(context.endsWith(file.getName())) {
                        if (event.kind() == StandardWatchEventKinds.ENTRY_CREATE || event.kind() == StandardWatchEventKinds.ENTRY_MODIFY) {
                            System.out.println("created / modified " + event.context());
                            new FileChangedEvent(file);
                            stop = true;
                        }
                        if (event.kind() == StandardWatchEventKinds.ENTRY_DELETE) {
                            //System.out.println("deleted " + event.context());
                        }
                    }
                }
            }

        } catch(Exception e) {
            System.out.println("File system error!");
            System.out.println(e.getMessage());
            System.exit(1);
        }

    }
}
