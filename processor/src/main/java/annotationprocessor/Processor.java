package annotationprocessor;

import com.squareup.javapoet.JavaFile;

import java.util.List;

public interface Processor {
    Class<?> getSupportedAnnotation();

    List<JavaFile> process();
}
