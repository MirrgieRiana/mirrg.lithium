# mirrg.lithium

- Repo: `https://raw.githubusercontent.com/MirrgieRiana/mirrg.lithium/master/maven`

## mirrg.applications.bin2csv

バイナリファイルとCSVファイルを相互変換するだけのGUIアプリケーション。
- 複数のファイルを一度に処理できる。
- ".csv"で終わっていたら中身を数値が改行で区切られたファイルと推測してバイナリにする。

## mirrg.applications.service.pwi

プロセスの再起動と標準入出力の管理を行うアプリケーション。
- 詳細な動作をプロパティファイルで設定できる。
- webから操作可能。

## mirrg.lithium.event

だいぶ簡素なイベントマネージャ。
- Exceptionのようにイベントの種類はクラス、イベントはインスタンスで管理される。

## mirrg.lithium.lang

雑多な静的関数をまとめただけのライブラリ。

## mirrg.lithium.parser

ジェネリクスを使って型安全なコンパイラを作るライブラリ。

## mirrg.lithium.properties

継承や他のプロパティの埋め込みができるプロパティファイル。
- 管理方式が文字列ではなく文字列を出力する関数であるため、プロパティの値をプログラム側で定義すればメソッドも呼べる。

## mirrg.lithium.struct

イミュータブル・ミュータブルなタプルを提供するだけ。

## mirrg.lithium.swing.util

SwingのGUIコンポーネントをJavaコード上で素早く組み立てるためのライブラリ。

## mirrg.lithium.template

新しいプロジェクトを作るためのテンプレートプロジェクト。

## mirrg.lithium.ui.input

マウスとキーボードからの入力を特有のプロトコルで提供するライブラリ。
