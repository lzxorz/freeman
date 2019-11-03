package com.freeman.common.constants;

/*interface Inter {
    public abstract void interFn();
}*/


/**
 * 枚举类 用关键字enum定义 , 实例用逗号分隔 , 最后一个实例后面写分号(如果只有声明实例这一行代码,也可以不写最后的分号)
 * 枚举类 不能继承(Enum<E extends Enum<E>>),可以实现接口
 * 枚举类 构造方法只能是private修饰(默认有一个private修饰的无参构造方法),可以自己定义private修饰的有参数的构造方法
 * 枚举类 可以有成员属性和成员方法
 * 枚举类 可以有抽象方法,每个实例都要重写
 *
 */
enum DemoEnum /*extends AbsClass*/ /*implements Inter*/ {
    ONE("1","这是1"){
        @Override
        public void absFn() {
            System.out.println("重写枚举类中的抽象方法by ONE");
        }
    },
    TWO("2","这是2"){
        @Override
        public void absFn() {
            System.out.println("重写枚举类中的抽象方法by TWO");
        }
    },
    THREE("3","这是3"){
        @Override
        public void absFn() {
            System.out.println("重写枚举类中的抽象方法by THREE");
        }
    };

    ////////////////////////////

    private String label;
    private String desc;

    /**
     * 枚举类自定义构造方法
     * 访问权限只能是 private
     */
    private DemoEnum(String label, String desc) {
        label = label;
        desc = desc;
    }

    public String getLabel() {
        return label;
    }

    public String getDesc() {
        return desc;
    }

    @Override
    public String toString() {
        return "DemoEnum{" + "label='" + label + '\'' + ", desc='" + desc + '\'' + '}';
    }

    ////////////////////////////
    /**
     * 枚举类重写接口的方法
     */
    /*@Override
    public void interFn() {
        System.out.println("枚举类实现了接口的方法.");
    }*/
    /**
     * 枚举类定义抽象方法,需要每个实例重写
     */
    public abstract void absFn();
}

public class TestEnum {
    public static void main(String[] args) {
        // 获取枚举类实例方式1
        DemoEnum one1 = DemoEnum.ONE;

        // 获取枚举类实例方式2
        DemoEnum one2 = Enum.valueOf(DemoEnum.class, "ONE");

        // 获取枚举类实例方式3
        DemoEnum one3 = DemoEnum.valueOf("ONE"); // 等效 DemoEnum one2 = DemoEnum.valueOf(DemoEnum.class, "ONE");

        /*one1.interFn();*/
        one1.absFn();
        System.out.println("枚举类实例的getLabel==>" + one1.getLabel());
        System.out.println("枚举类实例的getDesc==>"  + one1.getDesc());
        System.out.println("枚举类实例的toString==>" + one1.toString()); //等效 System.out.println(one);

        System.out.println("=======================================");

        //values() 获取枚举类所有实例
        for (DemoEnum en : DemoEnum.values()) {
            System.out.println("名字==>" + en.name() + ",序号==>" + en.ordinal());
        }
    }

    /*
    枚举类实现了接口的方法.
    重写枚举类中的抽象方法by ONE
    枚举类实例的getLabel==>1
    枚举类实例的getDesc==>这是1
    枚举类实例的toString==>DemoEnum{label='1', desc='这是1'}
    =======================================
    名字==>ONE,序号==>0
    名字==>TWO,序号==>1
    名字==>THREE,序号==>2
    */
}

