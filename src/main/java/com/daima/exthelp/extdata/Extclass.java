package com.daima.exthelp.extdata;

import com.daima.exthelp.InsertHandler.newEvent.InsertEvent;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.intellij.codeInsight.lookup.LookupElementBuilder;
import com.intellij.icons.AllIcons;
import com.intellij.openapi.util.Key;
import com.intellij.util.ProcessingContext;
import org.jetbrains.annotations.NotNull;

import java.util.*;

public class Extclass {
    @JsonProperty("Name")
    private final String name;

    @JsonProperty("Xtype")
    private final List<String> xtype;

    @JsonProperty("Config")
    private final List<Properties> config;

    @JsonProperty("Properties")
    private final List<Properties> properties;

    @JsonProperty("Methods")
    private final List<Methods> methods;

    @JsonProperty("Events")
    private final List<Events> events;

    @JsonProperty("Html")
    private final String html;

    @JsonProperty("Extends")
    private final String extendsClass;

    @JsonProperty("Mixins")
    private final List<String> mixins;

    // 存储根据前缀字母的所有代码提示项
    private Map<Character,LookupElementBuilder[]> lookupElementsByPrefix ;
    private static final Key<String> IS_EVENT = new Key<>("IS_EVENT");
    public static final Key<Events> EVENTDATA = new Key<>("EVENTDATA");

    public Extclass() {
        this.name = null;
        this.xtype = null;
        this.config = null;
        this.properties = null;
        this.methods = null;
        this.events = null;
        this.html = null;
        this.extendsClass = null;
        this.mixins =null;
    }
    public List<String> getMixins() {
        return mixins;
    }

    public static List<String> getAllMixinsAndSuperClasses(Extclass obj) {
        Set<String> allMixinsAndSuperClasses = new LinkedHashSet<>();
        allMixinsAndSuperClasses.add(obj.name);
        collectMixinsAndSuperClasses(obj, allMixinsAndSuperClasses);
        return new ArrayList<>(allMixinsAndSuperClasses);
    }

    private static void collectMixinsAndSuperClasses(Extclass obj, Set<String> allMixinsAndSuperClasses) {
        if (obj == null) {
            return;
        }

        // 添加当前对象的名称
        allMixinsAndSuperClasses.add(obj.getName());

        // 添加当前对象的mixins
        if (obj.getMixins() != null) {
            allMixinsAndSuperClasses.addAll(obj.getMixins());
        }

        // 递归处理父类
        String parentClassName = obj.getExtendsClass();
        if (parentClassName != null && !parentClassName.isEmpty()) {
            Extclass parentObj = Load.getInstance().getExtclassByName(parentClassName);
            collectMixinsAndSuperClasses(parentObj, allMixinsAndSuperClasses);
        }
    }

    public List<LookupElementBuilder> getSuperElementBuilder(){
        List<LookupElementBuilder> ret= new ArrayList<>();
        List<String> classList= getAllMixinsAndSuperClasses(this);
        for (String className : classList) {
            //System.out.println(className);
            ret.addAll(Load.getInstance().getExtclassByName(className).getLookupElementBuilder());
        }
        return ret;
    }

    public List<LookupElementBuilder> getLookupElementBuilder(){

        List<LookupElementBuilder> lookupElements = new ArrayList<>();

        for (Properties cfg : config) {
            if( cfg.getVisibility()!=0){
                //LookupElementBuilder builder = LookupElementBuilder.create(prop.getName())
                continue;
            }

            LookupElementBuilder builder = LookupElementBuilder.create(cfg.getName())
                    .withTypeText("Property")
                    .withIcon(AllIcons.General.Gear)
                    .withTailText(" " + String.join(", ", cfg.getValueType()), true);
            if (cfg.isReadOnly()) {
                builder = builder.withTailText(" (readonly)", true);
            }
            lookupElements.add(builder);
        }
        for (Properties prop : properties) {
            if( prop.getVisibility()!=0){
                //LookupElementBuilder builder = LookupElementBuilder.create(prop.getName())
                continue;
            }
            LookupElementBuilder builder = LookupElementBuilder.create(prop.getName())
                    .withTypeText("Property")
                    .withIcon(AllIcons.Nodes.Property)
                    .withTailText(" " + String.join(", ", prop.getValueType()), true);
            if (prop.isReadOnly()) {
                builder = builder.withTailText(" (readonly)", true);
            }
            lookupElements.add(builder);
        }

        // 添加方法
        for (Methods method : methods) {
            if( method.getVisibility()!=0){
                //LookupElementBuilder builder = LookupElementBuilder.create(prop.getName())
                continue;
            }
            lookupElements.add(
                    LookupElementBuilder.create(method.getName())
                            .withTypeText("Method")
                            .withIcon(AllIcons.Nodes.Method)
            );
        }

        // 添加事件
        for (Events evt : events) {
            if(evt.getName().equals("afterrender")){
                var a="";
            }
            LookupElementBuilder e =LookupElementBuilder.create(evt.getName())
                    .withTypeText("Event")
                    .withIcon(AllIcons.Actions.Lightning)
                    .withInsertHandler(new InsertEvent.InlineInsertHandler());
            e.putUserData(IS_EVENT,"");
            e.putUserData(EVENTDATA,evt);

            ;
            lookupElements.add(e);

        }
        return lookupElements;
    }


    private void generateLookupElements() {

        lookupElementsByPrefix = new HashMap<>();
        //获得全部上级
        List<LookupElementBuilder> lookupElements= getSuperElementBuilder();

        Map<Character, List<LookupElementBuilder>> groupedLookupElements = new HashMap<>();
        //循环所有元素
        for (LookupElementBuilder element : lookupElements) {
            char prefix = element.getLookupString().isEmpty() ? '#' : element.getLookupString().charAt(0);

            // 使用computeIfAbsent初始化数组并添加元素
            groupedLookupElements.computeIfAbsent(
                    prefix,
                    k -> new ArrayList<>()
            ).add(element);
        }
        for (Map.Entry<Character, List<LookupElementBuilder>> entry : groupedLookupElements.entrySet()) {
            Character key = entry.getKey();
            List<LookupElementBuilder> value = entry.getValue();
            lookupElementsByPrefix.put(key, value.toArray(new LookupElementBuilder[0]));
        }
    }

    // 获取指定前缀的代码提示项
    public LookupElementBuilder[] getLookupElementsByPrefix(char prefix, @NotNull ProcessingContext context) {
        if(lookupElementsByPrefix==null){
            this.generateLookupElements();
        }
        LookupElementBuilder[] lebs=lookupElementsByPrefix.get(prefix);
        //查询所有数据事件的代码
        return lebs;
    }

    public String getName() {
        return name;
    }

    public List<String> getXtype() {
        return xtype;
    }

    public List<Properties> getConfig() {
        return config;
    }

    public List<Properties> getProperties() {
        return properties;
    }

    public List<Methods> getMethods() {
        return methods;
    }
    public List<Methods> getAllMethods() {
        // 使用 LinkedHashSet 保持插入顺序且不重复
        Set<Methods> allMethods = new LinkedHashSet<>(methods);

        // 获取所有父类和混入类
        List<String> classList = getAllMixinsAndSuperClasses(this);
        for (String className : classList) {
            Extclass superClass = Load.getInstance().getExtclassByName(className);
            if (superClass != null) {
                // 将父类和混入类的方法添加到集合中
                allMethods.addAll(superClass.methods);
            }
        }

        return new ArrayList<>(allMethods);
    }
    public List<Events> getEvents() {
        return events;
    }

    public String getHtml() {
        return html;
    }

    public String getExtendsClass() {
        return extendsClass;
    }
}