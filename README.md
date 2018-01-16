# autoplay-dq11-poker

## これは何?
PS4リモートプレイを使用して、ドラゴンクエスト11(以下、ドラクエ11)のポーカーを
ロイヤルストレートスライムが出るまで自動操作するツールです。
(副作用でダブルアップの10回連続も狙えます)

Spring Statemachineの学習が主目的なので、ツールのクオリティ・精度はあまり高くありません。

## 必要な環境
以下がインストールされている必要があります。

* PS4リモートプレイ
* Java8以上
* Maven (ビルドに使用)

動作確認は macOS High Sierra 10.13.2 で行っています。
Windowsでも動くとは思いますが、Macで動かすことを推奨します。

## 免責事項
本ツールはキーボードやマウスの自動操作を行いますが、
本ツールの自動操作により損害が生じても、当方は一切の責任を負いません。

## アーキテクチャ

### ツールで使用している技術
* Java
* Java Robot API
    * マウスとキーボードの自動操縦に利用
* Spring Boot
    * アプリケーションの基盤として利用
* Spring Statemachine
    * 状態遷移の管理に利用

### 動作概要
PS4リモートプレイをPC上で実行することで、PS4のゲームをキーボードで操作可能になります。
本ツールでは、JavaのRobot APIを使用して、キーボード操作の自動化を行います。

ドラクエ11のポーカーでは、基本的にEnterキー(=○ボタン)を押していれば進んでいきますが、
カードが配られた直後は下キーを押して"くばる"ボタンにカーソルを移動してから、Enterキーを
押す必要があります。

また、ロイヤルストレートスライムを当てる確率を高めるために、ロイヤルストレートスライムに
必要となるカードを残すように操作をする必要もあります。

そのため、本ツールではゲーム画面に"くばる"ボタンが表示されている状態(カード配布済み状態)
を認識し、配られたカードが必要かどうかを判定した上で適切なキー操作を行うように実装しています。

状態の認識は画像キャプチャを行い、比較元となる画像ファイルと画像の比較を行うことで実現します。
例えば、カード配布済み状態を認識するには、"くばる"ボタンが表示される領域を画像キャプチャし、
予め用意しておいた"くばる"ボタンの画像と比較し、一致すればカード配布済み状態であると
認識します。

ロイヤルストレートスライムの達成がキャプチャ画像から検出されたらツールを止めます。

### 状態遷移
本ツールはSpring Statemachineを使って内部で状態遷移を管理しています。

状態遷移で使用する状態は以下のとおりです。Statesのenumで定義されています。

|状態                       |説明                                           |
|---------------------------|-----------------------------------------------|
|PLAYING_POKER_STATE        |ポーカープレイ中を表す親状態                   |
|DEALT_CARDS_STATE          |カード配布済み状態                             |
|OTHER_STATE                |その他の状態 (Enterキーを押しておけばいい状態) |
|RETRY_OR_END_STATE         |リトライ・終了選択状態                         |
|FINAL_STATE                |終了状態                                       |

状態遷移はStateMachineがイベントを受信することで発生します。

イベント送信の判定および送信はEventDispatcherTaskクラスにより行われます。
このクラスはSpringの@Scheduledを利用して定期的に実行されるようになっており、
デフォルトでは2秒ごとに画面をキャプチャし、必要なイベントをStateMachineに送信します。

イベントの一覧は下記のとおりです。Eventsのenumで定義されています。

|イベント                   |説明                                                |
|---------------------------|----------------------------------------------------|
|DEAL_CARDS_EVENT           |"くばる"ボタンを検知した際に送信される              |
|ROYAL_STRAIGHT_SLIME_EVENT |ロイヤルストレートスライムを検出した際に送信される  |
|BEFORE_BET_COIN_EVENT      |かけ金入力欄を検出した際に送信される                |
|OTHER_EVENT                |上記のイベントに合致しなかった場合に送信される      |

イベントを受信した際の状態遷移と実行されるActionは以下のとおりです。
(各状態とイベントでは、末尾の"_STATE", "_EVENT"を省略)

|遷移元状態   |イベント       |遷移先状態   |実行Action                                  |
|-------------|---------------|-------------|--------------------------------------------|
|-            |(初期状態遷移) |OTHER        |PS4リモートプレイのウィンドウを前面に出す   |
|OTHER        |DEAL_CARDS     |DEALT_CARDS  |残すカードを選択して"くばる"ボタンを押下    |
|OTHER        |OTHER          |OTHER        |Enterキーを押す                             |
|DEALT_CARDS  |OTHER          |OTHER        |Enterキーを押す                             |
|PLAYING_POKER|ROYAL_STRAIG.. |FINAL        |PSボタンを押して中断し、状態遷移を終了する  |
|PLAYING_POKRE|BEFORE_BET_COIN|RETRY_OR_END |かけ金を入力してリトライ、または状態遷移終了|

状態遷移のポイントは以下の通り

* "くばる"ボタンを検出するとカード選択の動作を行う
* ロイヤルストレートスライムを検出すると状態遷移を終了する
* かけ金入力欄を検出すると設定に応じてリトライまたは状態遷移終了となる
    * かけ金入力欄は通常は表示されないが、キーの取りこぼしやタイミングの問題で、
      ポーカーを続けるかという問いに"いいえ"を答えてしまった場合に起こりうる予期せぬ状態
        * ポーカーを続けるか → いいえ → ポーカー画面の終了
          → OTHER_EVENTにより○ボタンが押される → ポーカーのディーラと話す
          → ポーカーをやるかの問いに"はい" → 再びポーカー画面
          → かけ金入力欄の表示
          → かけ金のデフォルト値が0なので、○を押しても進まない状態に陥る

## 使い方
大まかな手順は以下の通り。

1. 事前準備
2. 画像キャプチャの準備
    * "くばる"ボタンのキャプチャ。カードが配られた状態になったことの検知に使います。
    * ロイヤルストレートスライムを構成する各カードのキャプチャ。
        * スライムの10
        * スライムのJack
        * スライムのQueen
        * スライムのKing
        * スライムのA
    * かけ金入力欄(入力値は0)のキャプチャ。
3. 自動操作の実行

### 1. 事前準備
* カジノのコインをたくさん稼いでおいてください。
    * 幸いドラクエ11のスロットは甘く設定されているので、連射コントローラーや、
      PS4リモートプレイ + キー操作ツールを使えば簡単に稼げるはずです。
* ツールのビルド
    * 本ツールのソースコードをダウンロードして、`mvn package` でjarを生成します。
      target配下にjarファイルが生成されます。
    * ビルドせずに起動することも可能ですが、やり方の説明は割愛します。
* PS4リモートプレイを以下の設定で起動します
    * 画質: 540p (960x540)
    * フレームレート: 標準
* PS4リモートプレイのウィンドウをデスクトップの左上にくっつける
    * Windowsではフリーウェアのツールなどを駆使してください
    * ウィンドウは拡大しないでください
        * ゲーム画面(メニューバーなどは含まない)が960x540のサイズになっている必要があります
        * 手動でウィンドウサイズを変更してしまった場合は、ウィンドウサイズを元に戻してください
            * OSごとのウィンドウサイズ情報のリセットや外部ツールの使用など
    * ウィンドウはそのまま動かさないでください
* PS4リモートプレイのゲーム画面(メニューバーなどは含まない)の座標を計算
    * ウィンドウの位置とサイズ情報を取得します
        * Macの場合は、以下のapple scriptをターミナルから実行してください。
          出力のpositionに位置が、sizeに幅と高さが表示されます。
        ```bash
        osascript -e 'tell application "System Events" to get properties of first window of application process "RemotePlay"'
        ```
        * Windowsの場合はフリーのユーティリティなどを使用してください
            * [Kaciy Window Operation](https://www.vector.co.jp/soft/dl/winnt/util/se510818.html)というツールで実現できそうです(動作は未確認)
    * 以下の計算を行い、ゲーム画面の位置(game-screen.location-x, game-screen.location-y)を
      算出します。この値は、ツールを起動するときのオプションとして指定します。
        * game-screen.location-x = (ウィンドウの幅 - 960) / 2 + ウィンドウ位置のx座標
        * game-screen.location-y = (ウィンドウの高さ - 540) + ウィンドウ位置のY座標
    * なお、ゲーム画面の位置は以下のデフォルト値に設定されているため、
      計算結果がデフォルト値と一致する場合はツール起動時のオプション指定を省略できます。
        * game-screen.location-x = 0
        * game-screen.location-y = 45


### 2. 画像キャプチャの準備
以後のコマンドで、--game-screen.locationのオプションを指定しているところは、
事前準備で予め計算した値を指定してください。

#### 2.1 "くばる"ボタンのキャプチャ
* PS4リモートプレイからドラクエ11のポーカーをプレイします。
* カードが配られて画面中央に"くばる"ボタンが表示されている状態にします。
* 以下のコマンドでキャプチャを行います。

```bash
$ java -jar autoplay-dq11-poker-1.0.0.RELEASE.jar --mode=CAPTURE_DEAL_CARDS_BUTTON --game-screen.location-x=0 --game-screen.location-y=45
```

* captureディレクトリにdealCardsButton.pngが生成されているので、画像を開いて確認します。
  "くばる"ボタンが画像の中心に入っていればキャプチャ成功です。
    * "くばる"ボタンがずれていたり、はみだしているようであれば、--game-screen.locationの
      オプション値を調整します

#### 2.2 ロイヤルストレートスライムを構成するカードのキャプチャ
* スライムの10, J, Q, K, A のいずれかが出るまでポーカーをプレイします。
    * カードが出るのは配られたカードでも、ダブルアップ中でも構いません。
* カードが出たら以下のコマンドでキャプチャします。
    * --capture-card-number には、目的のカードが左から数えて何番目かを指定します。
        * 2を指定すれば、左から2番めのカードがキャプチャされます
    * --capture-card-typeにはキャプチャするカードの種別を指定します。
        * スライムの10: s10
        * スライムのJ: sj
        * スライムのQ: sq
        * スライムのK: sk
        * スライムのA: sa

```bash
$ java -jar autoplay-dq11-poker-1.0.0RELEASE.jar --mode=CAPTURE_CARD --game-screen.location.x=0 --game-screen.location-y=45 --capture-card-number=1 --capture-card-type=sj
```

* captureディレクトリにカード種別名に対応する画像ファイルが生成されているので、
  画像を確認します。
    * 目的のカードがキャプチャされていること
    * カードが置いてあるテーブルの色(背景色)がキャプチャ範囲に入ってないこと
    * キャプチャに失敗した場合は、--game-screen.location の値を調整して
      再度キャプチャをやり直します。
      その際には、"くばる"ボタンのキャプチャも同じ値でやり直してください。
* 5種類のカードが全てキャプチャできるまでこの手順を繰り返します。

### 2.3 かけ金入力欄(入力値は0)のキャプチャ。
* ポーカーでかけ金入力の画面を表示します。
* 入力欄を0に設定します。
* 以下のコマンドでキャプチャします。
```bash
$ java -jar autoplay-dq11-poker-1.0.0.RELEASE.jar --mode=CAPTURE_BET_COIN_INPUT --game-screen.location-x=0 --game-screen.location-y=45
```

* captureディレクトリにbetCoinInput.pngが生成されているので、画像を開いて確認します。
  かけ金入力欄が画像の中心に入っていればキャプチャ成功です。
    * かけ金入力欄がずれていたり、はみだしているようであれば、--game-screen.locationの
      オプション値を調整します。
      その際には、これまでのキャプチャも全て同じオプション値でキャプチャし直してください。


### 3. 自動操作の実行
* PS4リモートプレイでポーカーをプレイし、賭け金を決定する画面まで進めます。
    * このとき、PS4リモートプレイのウィンドウをキャプチャ時から動かさないようにしてください。
    * かけ金は0以外に設定しておく必要があります
* 以下のコマンドを実行します。

```bash
$ java -jar autoplay-dq11-poker-1.0.0.RELEASE.jar --mode=AUTOPLAY
```

* 自動実行を途中でやめたい場合は、マウスカーソルをデスクトップの左上の座標(0,0)に持っていき、
  しばらくそのままにしておくとアプリケーションを強制停止できます。

## 注意事項・制約事項
* 誤動作を避けるために、不要なアプリケーションは全て終了しておいてください。
* ウィンドウやポップアップがPS4リモートプレイのウィンドウにかぶさると、正常に動作しません。
  各種アプリケーションの自動更新等でウィンドウが出ないようお気をつけください。
* Mac以外の環境では、ツール起動時にPS4リモートプレイのウィンドウをアクティブ化する際に、
  PS4リモートプレイのウィンドウの左上の方をマウスクリックするよう自動操作することで
  アクティブ化します。
  そのため、PS4リモートプレイのウィンドウの左上あたりがクリック可能な状況でツールを
  実行してください。
    * Macではapple scriptをつかってウィンドウを前にだすので、ウィンドウが完全に隠れていても
      問題ありません
* タイミングでカードや状態を正しく判定できないことがあります
    * 役が成立した場合、カードがたまに光るため、カードの判定に失敗することがあります
* ネットワーク遅延でキー操作が取りこぼされ、操作がずれてしまうことがあります。
* 理論的にはダブルアップの失敗時に開いた5枚のカードがロイヤルストレートスライムを
  構成するカードとなっていた場合、ロイヤルストレートスライムが達成されたと
  誤認する可能性があります。

## 主な実行時オプション
実行時にコマンドラインで指定する場合は "--OPTION=VALUE" の形で指定する。
"="の後の値はデフォルト値。
application.propertiesを書き換えることでも設定可能。

### mode=AUTOPLAY
動作モードを指定するオプション。下記の値が指定可能。

|設定可能な値              |説明                           |
|--------------------------|-------------------------------|
|AUTOPLAY                  |ポーカーの自動実行を行う       |
|CAPTURE_DEAL_CARDS_BUTTON |"くばる"ボタンをキャプチャする |
|CAPTURE_CARD              |カードをキャプチャする         |
|CAPTURE_BET_COIN_INPUT    |かけ金入力欄をキャプチャする   |

### capture-card-number=1
--mode=CAPTURE_CARDの場合に、キャプチャするカードの番号を指定するオプション。
1から5の整数を指定します(左側から1)。

### capture-card-type=s10
--mode=CAPTURE_CARDの場合に、キャプチャするカードの種別を指定するオプション。
保存されるファイル名に使用されます。

以下の値が指定可能です。

|設定可能な値 |説明            |
|-------------|----------------|
|s10          |スライムの10    |
|sj           |スライムのJack  |
|sq           |スライムのQueen |
|sk           |スライムのKing  |
|sa           |スライムのA     |

### game-screen.location-x=0, game-screen.location-y=45
PS4リモートプレイのウィンドウ内のゲーム画面の位置座標。
ウィンドウのメニューバーなどは含まない

### game-screen.width=960, game-screen.height=540
PS4リモートプレイのウィンドウ内のゲーム画面の幅と高さ。
ウィンドウのメニューバーなどは含まない。
基本的に960x540で固定。

### autoplay.dq11.poker.dealt-cards-capture.card-s10-filepath=capture/S10.png
スライムの10のカードのキャプチャ画像のファイルパス。

### autoplay.dq11.poker.dealt-cards-capture.card-s-j-filepath=capture/SJ.png
スライムのJのカードのキャプチャ画像のファイルパス。

### autoplay.dq11.poker.dealt-cards-capture.card-s-q-filepath=capture/SQ.png
スライムのQのカードのキャプチャ画像のファイルパス。

### autoplay.dq11.poker.dealt-cards-capture.card-s-k-filepath=capture/SK.png
スライムのKのカードのキャプチャ画像のファイルパス。

### autoplay.dq11.poker.dealt-cards-capture.card-s-a-filepath=capture/SA.png
スライムのAのカードのキャプチャ画像のファイルパス。

### autoplay.dq11.poker.dealt-cards-capture.x=85, 256, 427, 598, 769
配られた5枚のカードのゲーム画面内X座標。左からカード1枚め。

### autoplay.dq11.poker.dealt-cards-capture.y=195
配られた5枚のカードのゲーム画面内Y座標。

### autoplay.dq11.poker.dealt-cards-capture.width=105
配られた5枚のカードのゲーム画面内での幅。

### autoplay.dq11.poker.dealt-cards-capture.height=150
配られた5枚のカードのゲーム画面内での高さ。

### autoplay.dq11.poker.deal-cards-button-capture.filepath=capture/dealCardsButton.png
"くばる"ボタンのキャプチャ画像のファイルパス。

### autoplay.dq11.poker.deal-cards-button-capture.x=445
"くばる"ボタンのゲーム画面内でのX座標。

### autoplay.dq11.poker.deal-cards-button-capture.y=400
"くばる"ボタンのゲーム画面内でのY座標。

### autoplay.dq11.poker.deal-cards-button-capture.width=65
"くばる"ボタンのゲーム画面内での幅。

### autoplay.dq11.poker.deal-cards-button-capture.height=30
"くばる"ボタンのゲーム画面内での高さ。

### autoplay.dq11.poker.bet-coin-input-capture.filepath=capture/betCoinInput.png
かけ金入力欄のキャプチャ画像のファイルパス。

### autoplay.dq11.poker.bet-coin-input-capture.x=490
かけ金入力欄のゲーム内でのX座標。

### autoplay.dq11.poker.bet-coin-input-capture.y=440
かけ金入力欄のゲーム内でのY座標。

### autoplay.dq11.poker.bet-coin-input-capture.width=170
かけ金入力欄のゲーム内での幅。

### autoplay.dq11.poker.bet-coin-input-capture.height=80
かけ金入力欄のゲーム内での高さ。

### autoplay.dq11.poker.robot.autodelay=200
キー操作やマウス操作で使用するRobotのディレイ値。

### autoplay.dq11.poker.event.timer-interval=2000
イベントディスパッチャが画面キャプチャを取ってイベントを投げる処理を行うインターバル(ms)。

### autoplay.dq11.poker.event.capture-burst-count=3
イベントディスパッチャが画面キャプチャを連続で取る枚数。
役が揃ったときにカードが光るアニメーションが入り、カードが正しく認識出来ないことがあるので、
緩和策として短い時間で連続して画面キャプチャを行い、それぞれのキャプチャに対して
カードの認識を行い、結果を比較することでカード認識のミスを検出する。

### autoplay.dq11.poker.event.capture-burst-interval=200
イベントディスパッチャが画面キャプチャを連続で取る際のインターバル(ms)。

### autoplay.dq11.poker.event.debug-rss-event-count=0
ロイヤルストレートスライムが検出されたときの動作を確認するためのデバッグ用。
この値の数だけイベントディスパッチャがイベントを送信したときに、ロイヤルストレートスライムの
検出イベントを送信する。

### autoplay.dq11.poker.autoplay-config.application-exit-check-interval=5000
状態遷移マシンの停止およびユーザの停止指示(マウスカーソルを座標(0,0))に持っていいく)を
確認するインターバル(ms)。

### autoplay.dq11.poker.capture-history.max=10
アプリケーション終了時に保存する画面キャプチャの最大数。

### autoplay.dq11.poker.capture-history.save-directory=capture-log
アプリケーション終了時に画面キャプチャを保存するディレクトリ。

### autoplay.dq11.poker.capture-history.auto-old-file-delete=true
アプリケーション終了時にすでに画面キャプチャのファイルが存在していた場合に消すかどうか。

### autoplay.dq11.poker.window.activate-window-mode=auto
開始時にPS4リモートプレイのウィンドウをアクティブ化する際の動作モード指定。

|設定可能な値 |説明                                                      |
|-------------|----------------------------------------------------------|
|auto         |Macならapple-script、それ以外ならmouse-clickになる。      |
|apple-script |apple scriptを使ってアクティブ化する。                    |
|mouse-click  |マウスクリックでウィンドウをクリックしてアクティブ化する。|

### autoplay.dq11.poker.mouse-click-activate-window.mouse-click-x=10
マウスクリックでウィンドウをアクティブ化する際にクリックするゲーム画面内のX座標。
実際の座標はこの値にgame-screen.location-xの値が加算される。

### autoplay.dq11.poker.mouse-click-activate-window.mouse-click-y=55
マウスクリックでウィンドウをアクティブ化する際にクリックするゲーム画面内のY座標。
実際の座標はこの値にgame-screen.location-yの値が加算される。

### autoplay.dq11.poker.push-ps-button-action.ps-button-x=480
ゲームを中断する際に押すPSボタンのゲーム画面内のX座標。

### autoplay.dq11.poker.push-ps-button-action.ps-button-y=515
ゲームを中断する際に押すPSボタンのゲーム画面内のY座標。

### autoplay.dq11.poker.bet-coin-action.number-of-times-to-push-up-arrow=10
自動実行中にかけ金入力画面を検出し、リトライする際に、かけるコインの選択で上キーを押す回数。
最大で10。

### autoplay.dq11.poker.retry-push-deal-button-guard.wait-period=60000
カード配布済み状態で長時間状態が変化しない場合に、"くばる"ボタンの押下に失敗したとみなし
再度"くばる"ボタンを押下するまでの時間(ms)。

### autoplay.dq11.poker.retry-on-unexpected-guard.retry=true
自動実行中にかけ金入力画面を検出した際に、リトライするかどうか。
リトライしない場合はアプリケーションを終了する。
かけ金入力画面が検出されるのは通常ありえないが、キー操作の取りこぼしやタイミングなどで、
ポーカーを続けるか聞かれたときにいいえを答えてしまい、ポーカーをやめてしまった場合に
起こりうる。

### autoplay.dq11.poker.image-comparetor.min-diff=128
画像比較で一致とみなすしきい値。小さくすると比較が厳密になる。

### autoplay.dq11.poker.image-comparetor.max-diff=256
画像比較で警告付きの一致とみなすしきい値。小さくすると比較が厳密になる。

### logging.level.com.github.lenemarix.autoplay=info
アプリケーションログレベル。

## Future Work
* よりスマートで楽なキャプチャ画像の準備方法
* よりスマートな画像判定
* ネットワークが切れたときの検出および自動的に再接続
* ダブルアップチャンスの状態を認識する
    * ダブルアップチャンス10回連続の状態を検出する
    * ダブルアップが不要な場合、ダブルアップを常に断ることで時間短縮する
