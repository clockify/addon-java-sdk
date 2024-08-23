package com.cake.clockify.annotationprocessor;

import com.cake.clockify.annotationprocessor.clockify.ClockifyManifestProcessor;
import com.cake.clockify.annotationprocessor.clockify.ExtendClockifyManifest;
import com.google.auto.service.AutoService;
import com.squareup.javapoet.JavaFile;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedSourceVersion;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.DeclaredType;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import static com.cake.clockify.annotationprocessor.Constants.CLOCKIFY_MANIFESTS;

@AutoService(javax.annotation.processing.Processor.class)
@SupportedSourceVersion(SourceVersion.RELEASE_18)
@NoArgsConstructor
public class ManifestExtensionProcessor extends javax.annotation.processing.AbstractProcessor {

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Set.of(ExtendClockifyManifest.class.getCanonicalName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        for (TypeElement annotation : annotations) {
            processAnnotation(annotation, roundEnv);
        }

        return true;
    }

    @SneakyThrows
    private void processAnnotation(TypeElement annotation, RoundEnvironment roundEnv) {
        Set<? extends Element> elements = roundEnv.getElementsAnnotatedWith(annotation);
        List<JavaFile> files = new LinkedList<>();

        for (Element element : elements) {
            DeclaredType type = (DeclaredType) element.asType();

            for (String manifestPath: CLOCKIFY_MANIFESTS) {
                try {
                    files.addAll(new ClockifyManifestProcessor(type, manifestPath).process());
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException(e);
                }
            }
        }

        for (JavaFile file : files) {
            file.writeTo(processingEnv.getFiler());
        }
    }
}
