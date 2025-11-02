package app.service;

import app.model.Library;


import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class Repository {
    private static final int MAGIC = 0x53434F4C; // 'SCOL'
    private static final int VERSION = 1;


    public void save(Path file, Library library) throws IOException {
        Files.createDirectories(file.getParent());
        try (OutputStream fos = Files.newOutputStream(file);
             DataOutputStream dos = new DataOutputStream(fos)) {
            dos.writeInt(MAGIC);
            dos.writeInt(VERSION);
            try (ObjectOutputStream oos = new ObjectOutputStream(dos)) {
                oos.writeObject(library);
            }
        }
    }


    public Library load(Path file) throws IOException, ClassNotFoundException {
        try (InputStream fis = Files.newInputStream(file);
             DataInputStream dis = new DataInputStream(fis)) {
            int magic = dis.readInt();
            if (magic != MAGIC) throw new IOException("Bad file format: magic mismatch");
/* int version = dis.readInt(); // possible future handling based on version */
dis.readInt(); // possible future handling based on version
try (ObjectInputStream ois = new ObjectInputStream(dis)) {
    return (Library) ois.readObject();
}
        }
    }
}
