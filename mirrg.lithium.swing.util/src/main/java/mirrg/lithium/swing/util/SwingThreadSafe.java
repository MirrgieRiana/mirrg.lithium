package mirrg.lithium.swing.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * このメソッドをAWTイベントディスパッチャースレッド以外の
 * スレッドから呼び出してもかまわないことを宣言します。
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.SOURCE)
public @interface SwingThreadSafe
{

}
