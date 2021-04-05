package models;

import java.util.Arrays;
import java.util.List;

/**
 * This class is aimed to save emoticon for happy and sad sentiment get from website motivated by
 * https://www.piliapp.com/emoji/list/#tag Using "|" delimiter to generate pattern from two emoji
 * list (HAPPY and SAD)
 *
 * @author Junwei Zhang
 */
public class Emoji {

  /**
   * The constant HAPPY.
   */


  public static final List<String> HAPPY = Arrays.asList("\uD83D\uDE00", "\uD83D\uDE01",
      "\uD83D\uDE02", "\uD83E\uDD23", "\uD83D\uDE03",
      "\uD83D\uDE04", "\uD83D\uDE05", "\uD83D\uDE06", "\uD83D\uDE09", "\uD83D\uDE0A",
      "\uD83D\uDE0B", "\uD83D\uDE0E",
      "\uD83D\uDE0D", "\uD83D\uDE42", "\uD83E\uDD17", "\uD83E\uDD29", "\uD83D\uDE0F",
      "\uD83D\uDE3A",
      "\uD83D\uDE38", "\uD83D\uDE39", "\uD83D\uDE3B", "\uD83D\uDE3C", "\uD83D\uDC4D");

  /**
   * The constant SAD.
   */


  public static final List<String> SAD = Arrays
      .asList("\uD83D\uDE1E", "\uD83D\uDE12", "\uD83D\uDE44", "\uD83D\uDE1F", "\uD83D\uDE20",
          "\uD83D\uDE15", "\uD83D\uDE41", "\uD83E\uDD7A", "\uD83D\uDE23", "\uD83D\uDE16",
          "\uD83D\uDE2B",
          "\uD83D\uDE29", "\uD83D\uDE28", "\uD83D\uDE30", "\uD83D\uDE26", "\uD83D\uDE27",
          "\uD83D\uDE22", "\uD83D\uDE25",
          "\uD83D\uDE2A", "\uD83D\uDE2D", "\uD83E\uDD26", "\uD83E\uDD26\u200D", "\uD83D\uDC4E",
          "\uD83D\uDE4D\u200D", "\uD83D\uDE4D", "\uD83D\uDE4D\u200D", "\uD83D\uDC94",
          "\uD83D\uDE24");

  /**
   * The constant HA.
   */
  public static final String HA = String.join("|", HAPPY);
  /**
   * The constant SA.
   */
  public static final String SA = String.join("|", SAD);
}
