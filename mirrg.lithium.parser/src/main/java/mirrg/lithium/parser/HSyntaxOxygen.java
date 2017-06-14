package mirrg.lithium.parser;

import java.util.function.Function;
import java.util.function.Supplier;

import mirrg.lithium.parser.core.Node;
import mirrg.lithium.parser.core.Syntax;
import mirrg.lithium.parser.syntaxes.SyntaxExtract;
import mirrg.lithium.parser.syntaxes.SyntaxMap;
import mirrg.lithium.parser.syntaxes.SyntaxNamed;
import mirrg.lithium.parser.syntaxes.SyntaxOptional;
import mirrg.lithium.parser.syntaxes.SyntaxOr;
import mirrg.lithium.parser.syntaxes.SyntaxPack;
import mirrg.lithium.parser.syntaxes.SyntaxRegex;
import mirrg.lithium.parser.syntaxes.SyntaxRepeat;
import mirrg.lithium.parser.syntaxes.SyntaxSerial;
import mirrg.lithium.parser.syntaxes.SyntaxSlot;
import mirrg.lithium.parser.syntaxes.SyntaxString;
import mirrg.lithium.parser.syntaxes.SyntaxTunnel;

public class HSyntaxOxygen
{

	/**
	 * 正規表現にマッチする最長の部分を切り出す末端ノードです。
	 */
	public static Syntax<String> regex(String regex)
	{
		return pack(new SyntaxRegex(regex), t -> t.group());
	}

	/**
	 * 正規表現にマッチする部分のMatcherを得る末端ノードです。
	 */
	public static SyntaxRegex regexMatcher(String regex)
	{
		return new SyntaxRegex(regex);
	}

	/**
	 * 文字列に一致する部分を切り出す末端ノードです。
	 */
	public static SyntaxString string(String string)
	{
		return new SyntaxString(string);
	}

	/**
	 * 異なる種類のノードを直列に連結して一つのノードにします。
	 */
	public static <T> SyntaxSerial<T> serial(Supplier<T> supplier)
	{
		return new SyntaxSerial<>(supplier);
	}

	/**
	 * 0回以上繰り返される同一のノードを表します。
	 */
	public static <T> SyntaxRepeat<T> repeat(Syntax<T> syntax)
	{
		return repeat(syntax, 0, Integer.MAX_VALUE);
	}

	/**
	 * 1回以上繰り返される同一のノードを表します。
	 */
	public static <T> SyntaxRepeat<T> repeat1(Syntax<T> syntax)
	{
		return repeat(syntax, 1, Integer.MAX_VALUE);
	}

	/**
	 * min以上max以下の回数繰り返される同一のノードを表します。
	 */
	public static <T> SyntaxRepeat<T> repeat(Syntax<T> syntax, int min, int max)
	{
		return new SyntaxRepeat<>(syntax, min, max);
	}

	/**
	 * 複数のノードの候補のうち最初にマッチしたものを表すノードです。
	 *
	 * @param dummy
	 *            型引数Tを与えるためだけの引数です。通常はnullをキャストして指定します。
	 */
	public static <T> SyntaxOr<T> or(T dummy)
	{
		return new SyntaxOr<>();
	}

	/**
	 * 0回以上1回以下繰り返されるノード列を表します。
	 */
	public static <T> SyntaxOptional<T> optional(Syntax<T> syntax)
	{
		return new SyntaxOptional<>(syntax);
	}

	/**
	 * ノードの値を書き換えます。
	 * このAPIはノードを生成しません。
	 *
	 * @deprecated
	 * 			このAPIでノードの値を書き換えることは推奨されません。
	 *             代わりに{@link #pack(Syntax, Function)}を用いてください。
	 */
	@Deprecated
	public static <I, O> SyntaxMap<I, O> map(Syntax<I> syntax, Function<I, O> function)
	{
		return new SyntaxMap<>(syntax, n -> function.apply(n.value));
	}

	/**
	 * ノード自体を異なる値に置き換えます。
	 * このAPIはノードを生成しません。
	 *
	 * @deprecated
	 * 			このAPIでノードの値を書き換えることは推奨されません。
	 *             代わりに{@link #pack(Syntax, Function)}を用いてください。
	 */
	@Deprecated
	public static <I, O> SyntaxMap<I, O> mapNode(Syntax<I> syntax, Function<Node<I>, O> function)
	{
		return new SyntaxMap<>(syntax, function);
	}

	/**
	 * ノードの値を変換するノードを表します。
	 */
	public static <I, O> SyntaxPack<I, O> pack(Syntax<I> syntax, Function<I, O> function)
	{
		return new SyntaxPack<>(syntax, n -> function.apply(n.value));
	}

	/**
	 * ノード自体を別の値に変換するノードを表します。
	 */
	public static <I, O> SyntaxPack<I, O> packNode(Syntax<I> syntax, Function<Node<I>, O> function)
	{
		return new SyntaxPack<>(syntax, function);
	}

	/**
	 * 型引数を簡単にキャストするために存在します。
	 * 例えばSyntac&lt;String&gt;をSyntax&lt;Object&gt;型に変換するために用います。
	 * このAPIはノードを生成しません。
	 */
	public static <I extends O, O> Syntax<O> wrap(Syntax<I> syntax)
	{
		return map(syntax, i -> i);
	}

	/**
	 * 文法を遅延して代入するための箱です。
	 * このAPIはノードを生成しません。
	 */
	public static <T> SyntaxSlot<T> slot()
	{
		return new SyntaxSlot<>();
	}

	/**
	 * 文法を遅延して評価するための箱です。
	 * このAPIはノードを生成しません。
	 */
	public static <T> SyntaxSlot<T> slot(Supplier<Syntax<T>> supplier)
	{
		return new SyntaxSlot<>(supplier);
	}

	/**
	 * {@link #serial(Supplier)}のうち、指定の一つの要素だけを抽出するために用います。
	 * このAPIは内部的に{@link #serial(Supplier)}と{@link #pack(Syntax, Function)}で構成されます。
	 *
	 * @param dummy
	 *            型引数Tを与えるためだけの引数です。通常はnullをキャストして指定します。
	 * @deprecated
	 * 			このAPIは内部のextract節で指定されたノードのタグが支配する範囲を拡張し、
	 *             想定外の範囲でタグの特殊効果を引き起こします。
	 *             代わりに{@link #tunnel}を使うことが推奨されます。
	 */
	@Deprecated
	public static <T> SyntaxExtract<T> extract(T dummy)
	{
		return new SyntaxExtract<>();
	}

	/**
	 * 基本的な動作は{@link #extract}に準じますが、
	 * このAPIは抽出されるタグの支配範囲を拡張しません。
	 *
	 * @param dummy
	 *            型引数Tを与えるためだけの引数です。通常はnullをキャストして指定します。
	 */
	public static <T> SyntaxTunnel<T> tunnel(T dummy)
	{
		return new SyntaxTunnel<>();
	}

	/**
	 * ノードに名前を与えます。
	 * 名前が与えられたノードの下位のノードはトークン予測のための処理を行わず、代わりに名前が用いられます。
	 * 名前が与えられていない場合、文字列や正規表現ノードを表現する文字列が代わりに使われます。
	 */
	public static <T> SyntaxNamed<T> named(Syntax<T> syntax, String name)
	{
		return new SyntaxNamed<>(syntax, name);
	}

}
