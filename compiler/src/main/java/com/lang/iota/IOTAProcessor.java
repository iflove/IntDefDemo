package com.lang.iota;


import com.google.auto.service.AutoService;
import com.lang.annotation.iota.IOTA;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.Modifier;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementFilter;

@AutoService(Processor.class)
public class IOTAProcessor extends AbstractProcessor {
    private Filer filer;

    @Override
    public synchronized void init(ProcessingEnvironment processingEnvironment) {
        super.init(processingEnvironment);
        filer = processingEnv.getFiler();
    }

    @Override
    public Set<String> getSupportedOptions() {
        return Collections.singleton("org.gradle.annotation.processing.aggregating");
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }

    @Override
    public Set<String> getSupportedAnnotationTypes() {
        return Collections.singleton(IOTA.class.getCanonicalName());
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnvironment) {
        if (annotations == null || annotations.isEmpty()) {
            return false;
        }
        //1.获取IOTA注解上的所有元素
        Set<? extends Element> elements = roundEnvironment.getElementsAnnotatedWith(IOTA.class);
        if (elements != null) {
            for (Element element : elements) {
                ElementKind kind = element.getKind();
                //2.过滤非枚举类
                if (kind == ElementKind.ENUM) {
                    //3.获取相关必要信息: 注解的位置包名,类名等
                    String pathName = element.asType().toString();
                    String enumClassName = element.getSimpleName().toString();
                    String className = enumClassName + "Def";
                    String pkgName = "";
                    if (pathName.contains(".")) {
                        pkgName = pathName.substring(0, pathName.lastIndexOf("."));
                    }
                    //解析注解 name 参数
                    IOTA iota = element.getAnnotation(IOTA.class);
                    if (iota != null) {
                        String name = iota.name();
                        if (!"".equals(name)) {
                            className = name;
                        }
                    }

                    System.out.println(String.format("find iota pkg: %s , className: %s", pkgName, className));


                    //4.构建常量
                    List<VariableElement> variableElements = ElementFilter.fieldsIn(element.getEnclosedElements());
                    List<FieldSpec> fieldSpecs = new ArrayList<>();
                    for (VariableElement variableElement : variableElements) {
                        ElementKind variableElementKind = variableElement.getKind();
                        if (variableElementKind == ElementKind.ENUM_CONSTANT) {
                            String name = variableElement.getSimpleName().toString();
                            FieldSpec.Builder builder = FieldSpec.builder(TypeName.INT, name, Modifier.PUBLIC, Modifier.FINAL, Modifier.STATIC);
                            builder.initializer(enumClassName + "." + name + ".ordinal()");
                            fieldSpecs.add(builder.build());
                        }
                    }

                    //5.构建类体
                    TypeSpec iotaDefClass = TypeSpec.classBuilder(className)
                            .addModifiers(Modifier.PUBLIC, Modifier.FINAL)
                            .addFields(fieldSpecs)
                            .build();

                    try {
                        JavaFile build = JavaFile.builder(pkgName, iotaDefClass).build();
                        build.writeTo(System.out);
                        build.writeTo(filer);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
        return false;
    }
}
