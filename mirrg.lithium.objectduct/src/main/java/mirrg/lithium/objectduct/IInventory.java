package mirrg.lithium.objectduct;

public interface IInventory
{

	/**
	 * このインベントリの内部ネットワークを構築します。
	 * ネットワークはインベントリの定義・コネクションの定義の順に初期化すべきです。
	 */
	public void init() throws Exception;

	/**
	 * このインベントリのオートメーション動作を開始します。
	 * {@link #stop()} された後に再び呼び出すことはできません。
	 */
	public void start() throws Exception;

	/**
	 * このインベントリのオートメーション動作を終了します。
	 */
	public void stop();

	/**
	 * このインベントリのオートメーション動作を行うスレッドが終了するまで待機します。
	 * このメソッドの呼び出し後も依然としてインベントリはオブジェクトを出力する可能性があります。
	 */
	public void join() throws InterruptedException;

}
