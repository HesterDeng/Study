package com.annotation;


import com.tunnelkey.tktim.model.log.ActionType;
import com.tunnelkey.tktim.model.log.FunctionModule;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * 用户行为注解
 * 用于方法上，把用户重要的操作记录给记录下来
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface UserActionFilter {

    /**
     * 模块
     *
     * @return
     */
    FunctionModule m() default FunctionModule.tgis;

    /**
     * 动作名称
     *
     * @return
     */
    String a();

    /**
     * 执行什么动作
     *
     * @return
     */
    ActionType t() default ActionType.add;

    /**
     * 进入界面后的主键
     * 隧道，工作面有header 这个k是辅助直接进入直接面的key
     *
     * @return
     */
    String k() default "";

    /**
     * 页面功能code
     *
     * @return
     */
    String v();

    /**
     * 输出主键
     * 如果输入主键定位不到数据，通过这个主键，获取补充k的值
     *
     * @return
     */
    String outk() default "";

}
