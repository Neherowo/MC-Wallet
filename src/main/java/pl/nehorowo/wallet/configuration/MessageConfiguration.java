package pl.nehorowo.wallet.configuration;

import eu.okaeri.configs.OkaeriConfig;
import eu.okaeri.configs.annotation.Header;
import lombok.Getter;
import pl.nehorowo.wallet.notice.Notice;
import pl.nehorowo.wallet.notice.NoticeType;

@Getter

@Header("Plik konfiguracyjny messages.yml")
@Header("")
@Header(" Wszystkie wiadomości w pluginie są konfigurowalne.")
@Header(" Plugin wspiera HEX kolory. Ich format to: &#0000ff (gdzie 0000ff to kolor w HEX).")
@Header(" Dziękuje za uzywanie pluginu.")
public class MessageConfiguration extends OkaeriConfig {

    private Notice noPermission = new Notice(NoticeType.MESSAGE, "&cNie posiadasz uprawnień do tej komendy.");
    private Notice correctUsage = new Notice(NoticeType.MESSAGE, "&cPoprawne użycie: &7[USAGE]");
    private Notice incorrectPlayer = new Notice(NoticeType.MESSAGE, "&cNie znaleziono gracza o podanej nazwie.");

    private Notice reloaded = new Notice(NoticeType.SUBTITLE, "&aPrzeladowano plugin.");
    private Notice wrongAmount = new Notice(NoticeType.MESSAGE, "&cPodana kwota jest nieprawidłowa.");
    private Notice notEnoughtMoney = new Notice(NoticeType.MESSAGE, "&cNie posiadasz wystarczającej ilości pieniędzy na koncie.");
    private Notice addedMoney = new Notice(NoticeType.MESSAGE, "&8** &7Dodano &a[AMOUNT]wPLN &7do twojego konta.");
    private Notice addedMoneyAdmin = new Notice(NoticeType.MESSAGE, "&8** &7Dodano &a[AMOUNT]wPLN &7do konta gracza &2[PLAYER].");
    private Notice removeMoney = new Notice(NoticeType.MESSAGE, "&8** &7Dodano &a[AMOUNT]wPLN &7do twojego konta.");
    private Notice removeMoneyAdmin = new Notice(NoticeType.MESSAGE, "&*** &7Usunieto &a[AMOUNT]wPLN &7z konta gracza &2[PLAYER]");
    private Notice setMoney = new Notice(NoticeType.MESSAGE, "&8** &7Ustawiono twoje &awPLN&7 na &2[AMOUNT].");
    private Notice setMoneyAdmin = new Notice(NoticeType.MESSAGE, "&8** &7Ustawiono &awPLN&7 gracza &2[PLAYER]&7 na &a[AMOUNT].");
    private Notice youHave = new Notice(NoticeType.SUBTITLE, "&aMasz [MONEY]wPLN.");

}
