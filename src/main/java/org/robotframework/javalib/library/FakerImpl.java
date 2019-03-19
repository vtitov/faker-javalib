package org.robotframework.javalib.library;

import io.github.classgraph.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import lombok.val;
import org.robotframework.javalib.factory.KeywordFactory;
import org.robotframework.javalib.keyword.Keyword;
import org.robotframework.javalib.library.faker.Logging;
import org.robotframework.javalib.library.faker.RobotUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static org.apache.commons.lang3.StringUtils.capitalize;
import static org.robotframework.javalib.library.faker.Logging.*;
import static org.robotframework.javalib.library.faker.RobotUtils.*;

public class FakerImpl extends KeywordFactoryBasedLibrary<Keyword>
    implements KeywordDocumentationRepository {

    //private static IKeywordNameNormalizer keywordNameNormalizer = new KeywordNameNormalizer();

    @AllArgsConstructor(staticName="apply")
    @ToString
    static class FakerMethod implements Keyword {
        @Getter private FakerImpl robotFakerImpl;
        @Getter private String robotName;
        @Getter private MethodInfo fieldMethod;
        @Getter private Collection<MethodInfo> subMethods;

        @Override
        public Object execute(Object[] arguments) {
            for(val smi:subMethods){
                if(smi.getParameterInfo().length == arguments.length) {
                    try {
                        val field = fieldMethod.loadClassAndGetMethod().invoke(robotFakerImpl.faker);
                        //return smi.loadClassAndGetMethod().invoke(field);
                        return smi.loadClassAndGetMethod().invoke(field, arguments);
                    } catch (IllegalAccessException|IllegalArgumentException|InvocationTargetException e) {
                        error(String.format("invoke error: %s", this), e);
                        throw new IllegalArgumentException(e);
                    }
                }
            }
            val e = new IllegalArgumentException(
                String.format("can't execute keyword %s with arguments %s", this, Arrays.toString(arguments))
            );
            error("", e);
            throw e;
        }
    }


    public UUID fakerRandomUUID() {
        return UUID.randomUUID();
    }

    //@Delegate(types=com.github.javafaker.Faker.class)
    private final transient com.github.javafaker.Faker faker;
    private final transient ScanResult  robotFakerScanResult;

    public FakerImpl(String ...argv) {
        val args = Arrays.asList(argv);
        Logging.trace("FakerImpl w/ args: " + args);
        val locale = Locale.forLanguageTag(argv.length <= 0 ? "en" : args.get(0));
        val seed = argv.length <= 1 ? new Random() : new Random(Long.parseLong(argv[1]));
        assert argv.length <=2;
        faker = new com.github.javafaker.Faker(locale, seed);
        robotFakerScanResult = new ClassGraph()
            .enableMethodInfo()
            .whitelistPackagesNonRecursive(faker.getClass().getPackage().getName())
            .scan();
    }

   @Override
    protected KeywordFactory<Keyword> createKeywordFactory() {
        val hashtable = createKeywordsMap();

        return new KeywordFactory<Keyword>(){
            @Override
            public Keyword createKeyword(String keywordName) {
                //return hashtable.getOrDefault(normalize(keywordName),null);
                return hashtable.getOrDefault(keywordName,null);
            }

            @Override
            public String[] getKeywordNames() {
                //trace(Arrays.toString(hashtable.keySet().toArray(new String[0])));
                return hashtable.keySet().toArray(new String[0]);
            }
        };
    }

    @Override
    public String getKeywordDocumentation(String keywordName) {
        //trace("get keyword documentation: %s", keywordName);
        if (keywordName.equals("__intro__")) {
            return
                "Provides utility methods from com.github.javafaker for generating fake strings," +
                    " such as names, phone numbers, addresses." +
                    " generate random strings with given patterns";
        }
        return String.format("%s", keywordName);
    }

    @Override
    public String[] getKeywordArguments(String keywordName) {
        try {
            //trace("getKeywordArguments: %s", keywordName);
            return Optional
                .ofNullable((FakerMethod) getKeywordFactory().createKeyword(keywordName))
                .map(kw -> kw.getSubMethods().stream()
                    .max(Comparator.comparingInt(mi -> mi.getParameterInfo().length))
                    .orElse(null))
                .map(longest ->
                    Arrays.stream(longest.getParameterInfo())
                        //    .map(MethodParameterInfo::getName).toArray(String[]::new)) // TODO get names of parameters from faker java lib compiled for JDK version 8+ with the commandline switch `-parameters`
                        .map(MethodParameterInfo::getTypeSignatureOrTypeDescriptor)
                        .map(TypeSignature::toString)
                        .map(RobotUtils::simplifyTypeName)
                        .map(simpleName->
                            (isVowel(simpleName.charAt(0)) ? "an" : "a") + capitalize(simpleName))
                        .toArray(String[]::new)
                )
                .orElse(new String[0]);
        } catch (Exception e) {
            error(String.format("error while getting keyword %s arguments: ", keywordName), e);
            throw e;
        }
    }

    private Map<String,Keyword> createKeywordsMap() {
        val hashtable = new TreeMap<String, Keyword>();
        val classesMap = this.robotFakerScanResult.getAllClassesAsMap();
        for (val es: getClassNonStaticMethods(classesMap.get(this.faker.getClass().getTypeName())).entrySet()) {
            for (val m: es.getValue()) {
                //trace(String.format("method %s: ()-> %s", m.getName(), m.getTypeSignatureOrTypeDescriptor().getResultType()));
                for (val es2: getClassNonStaticMethods(classesMap.get(m.getTypeSignatureOrTypeDescriptor().getResultType().toString())).entrySet()) {
                    val fm = FakerMethod.apply(
                        this,
                        capitalize(es.getKey()) + capitalize(es2.getKey()),
                        m,
                        es2.getValue()
                    );
                    //hashtable.put(fm.getRobotName(),fm);
                    //hashtable.put(normalize(fm.getRobotName()),fm);
                    hashtable.put(splitBySpaces(fm.getRobotName()),fm);
                }
            }
        }
        return hashtable;
    }

    //    @Override
    //    public String[] getKeywordNames() {
    //        val kws = super.getKeywordNames();
    //        trace(String.format("keywords: %s", Arrays.toString(kws)));
    //        return kws;
    //        //return super.getKeywordNames();
    //    }
    //
    //    @Override
    //    public Object runKeyword(String keywordName, Object[] args) {
    //        try {
    //            return super.runKeyword(keywordName, args);
    //        } catch (RuntimeException e) {
    //            throw retrieveInnerException(e);
    //        }
    //    }
    //    private RuntimeException retrieveInnerException(RuntimeException e) {
    //        Throwable cause = e.getCause();
    //        if (cause != null && InvocationTargetException.class.equals(cause.getClass())) {
    //            Throwable original = cause.getCause();
    //            return new RuntimeException(original.getMessage(), original);
    //        }
    //        return e;
    //    }


    //    /*public*/ String fakerResolve(String key) {
    //        return faker.resolve(key);
    //    }
    //
    //    /*public*/ String fakerInvoke(String ...argv) {
    //        //return faker.resolve(key);
    //        return null;
    //    }





}
