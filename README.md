# ComittoNxA

個人で楽しむために作成しています。    
ソースの再利用は自由です。  
利用しているライブラリはそれぞれのライセンスに従ってください。  

対応バージョンはAndroid2.3以上です。  
動作確認は1台でしか行っていません。(Android10.0)  

圧縮ファイルのいくつかが最終ページから開くバグが発生中。  
そんなところいじった覚えないのに…  

## 変更履歴

### Ver1.65A1 (2020/08/02)

Android2.3以上。  
Android7～9でSDカードや内部ドライブのファイルの操作ができるかは不明です。  

  - SMB2の共有フォルダアクセスに対応。
  - SDカードのファイルの名前の変更・削除に対応。
  - ツールバーの順番やアイコンを変更。
  - フォルダのサムネイル作成時にフォルダ内のファイルから作成できなければサブフォルダを対象にする。
  - サムネイル作成時に余白を削除。背表紙と折り返しをなるべく避ける。
  - サムネイル表示でタイトルを()や[]で分解して表示。
  - ソートで「前」「後」や漢数字「一、二、三…」(壱、弐、参…)に対応。
  - ソートの数字の比較でマイナス、カンマ、小数点に対応。
  - 初期フォルダより上にのフォルダ移動した場合に、フォルダ内が参照できなくても初期フォルダまでの途中のフォルダを表示するよう変更。
  - 3回連続で起動してタッチするまでに異常終了した場合には、リスト表示(サムネイルOFF)にして初期フォルダから起動するよう変更。
  - ページ結合と余白カット方法をより画面にフィットするように変更。
  - 先頭・末尾移動ボタンの長押し判定する範囲を変更。
