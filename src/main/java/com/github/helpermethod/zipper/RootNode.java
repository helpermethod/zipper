package com.github.helpermethod.zipper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;

public class RootNode implements Node {
    private List<Node> children = new ArrayList<>();

    @Override
    public void accept(Visitor visitor) throws IOException {
        visitor.visit(this);
    }

    public RootNode file(String name, String content) {
        children.add(new FileNode(name, content));

        return this;
    }

    public RootNode directory(String name, Consumer<DirectoryNode> block) {
            DirectoryNode zipParentNode = new DirectoryNode(name + "/");
            block.accept(zipParentNode);

            children.add(zipParentNode);

            return this;
    }

    List<Node> children() {
        return Collections.unmodifiableList(children);
    }
}
