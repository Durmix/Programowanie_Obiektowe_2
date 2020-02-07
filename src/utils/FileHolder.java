package utils;

import java.io.Serializable;

public class FileHolder implements Serializable {

    private String owner;

    private byte[] content;

    private String name;

    public FileHolder(String owner, byte[] content, String name)
    {
        this.owner = owner;
        this.content = content;
        this.name = name;
    }

    public byte[] getContent() {
        return content;
    }

    public String getName() {
        return name;
    }

    public String getOwner() {
        return owner;
    }

}
