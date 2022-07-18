package fr.menestis.commons.packets.scoreboard;

import org.bukkit.ChatColor;

public class Scroller {
    private int position;

    private final String str;

    private final String colorBefore;

    private final String colorAfter;

    private final String colorMid;

    private ChatColor textColor;

    private boolean upperCaseMid;

    private ScrollType scrollType;

    public Scroller(ChatColor textColor, String str, String colorMid, String colorBefore, String colorAfter, boolean upperCaseMid, ScrollType scrollType) {
        this.str = str;
        this.colorMid = colorMid;
        this.colorBefore = colorBefore;
        this.colorAfter = colorAfter;
        this.textColor = textColor;
        this.upperCaseMid = upperCaseMid;
        this.scrollType = scrollType;
        this.position = (scrollType == ScrollType.FORWARD) ? -1 : str.length();
    }

    public String getString() {
        return this.str;
    }

    public String next() {
        if (this.position >= this.str.length()) {
            String str1 = this.str.substring(this.position - 1, this.str.length() - 1);
            String str2 = this.upperCaseMid ? (this.colorMid + str1.toUpperCase()) : (this.colorMid + str1);
            String fin = this.textColor + this.str.substring(0, this.str.length() - 1) + this.colorBefore + this.str.substring(this.str.length() - 1, this.str.length()) + str2;
            if (getScrollType() == ScrollType.FORWARD) {
                this.position = -1;
            } else {
                this.position--;
            }
            return fin;
        }
        if (this.position <= -1) {
            if (getScrollType() == ScrollType.FORWARD) {
                this.position++;
            } else {
                this.position = this.str.length();
            }
            return this.colorBefore + this.str.substring(0, 1) + this.textColor + this.str.substring(1);
        }
        if (this.position == 0) {
            String str1 = this.str.substring(0, 1);
            String str2 = this.upperCaseMid ? (this.colorMid + str1.toUpperCase()) : (this.colorMid + str1);
            String fin = str2 + this.colorAfter + this.str.substring(1, 2) + this.textColor + this.str.substring(2);
            if (getScrollType() == ScrollType.FORWARD) {
                this.position++;
            } else {
                this.position--;
            }
            return fin;
        }
        String one = this.str.substring(0, this.position);
        String two = this.str.substring(this.position + 1);

        String three = this.upperCaseMid ? (this.colorMid + this.str.substring(this.position, this.position + 1).toUpperCase()) : (this.colorMid + this.str.substring(this.position, this.position + 1));
        String fin2;
        int m = one.length();
        int l = two.length();
        String first = (m <= 1) ? (this.colorBefore + one) : (one.substring(0, one.length() - 1) + this.colorBefore + one.substring(one.length() - 1));
        String second = (l <= 1) ? (this.colorAfter + two) : (this.colorAfter + two.charAt(0) + this.textColor + two.substring(1));
        fin2 = this.textColor + first + three + second;
        if (getScrollType() == ScrollType.FORWARD) {
            this.position++;
        } else {
            this.position--;
        }
        return fin2;
    }

    public int getPosition() {
        return this.position;
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int stringLength() {
        return this.str.length();
    }

    public ChatColor getTextColor() {
        return this.textColor;
    }

    public void setTextColor(ChatColor textColor) {
        this.textColor = textColor;
    }

    public boolean isUpperCaseMid() {
        return this.upperCaseMid;
    }

    public void setUpperCaseMid(boolean upperCaseMid) {
        this.upperCaseMid = upperCaseMid;
    }

    public ScrollType getScrollType() {
        return this.scrollType;
    }

    public void setScrollType(ScrollType scrollType) {
        this.scrollType = scrollType;
    }

    public enum ScrollType {
        FORWARD(),
        BACKWARD();
    }
}
