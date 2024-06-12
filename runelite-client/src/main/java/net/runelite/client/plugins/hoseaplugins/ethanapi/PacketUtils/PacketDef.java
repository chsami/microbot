package net.runelite.client.plugins.hoseaplugins.ethanapi.PacketUtils;

public class PacketDef {
    public final String name;
    public final String[] writeData;
    public final String[][] writeMethods;
    public final PacketType type;

    PacketDef(String var1, String[] writeData, String[][] writeMethods, PacketType type) {
        this.name = var1;
        this.writeData = writeData;
        this.writeMethods = writeMethods;
        this.type = type;
    }

    public static PacketDef getOpObj1() {
        String[] writeData = new String[]{ObfuscatedNames.OPOBJ1_WRITE1, ObfuscatedNames.OPOBJ1_WRITE2, ObfuscatedNames.OPOBJ1_WRITE3, ObfuscatedNames.OPOBJ1_WRITE4};
        String[][] writeMethods = ObfuscatedNames.OPOBJ1_WRITES;
        return new PacketDef(ObfuscatedNames.OPOBJ1_OBFUSCATEDNAME, writeData, writeMethods, PacketType.OPOBJ);
    }

    public static PacketDef getIfButton9() {
        String[] writeData = new String[]{ObfuscatedNames.IF_BUTTON9_WRITE1, ObfuscatedNames.IF_BUTTON9_WRITE2, ObfuscatedNames.IF_BUTTON9_WRITE3};
        String[][] writeMethods = ObfuscatedNames.IF_BUTTON9_WRITES;
        return new PacketDef(ObfuscatedNames.IF_BUTTON9_OBFUSCATEDNAME, writeData, writeMethods, PacketType.IF_BUTTON);
    }

    public static PacketDef getIfButton8() {
        String[] writeData = new String[]{ObfuscatedNames.IF_BUTTON8_WRITE1, ObfuscatedNames.IF_BUTTON8_WRITE2, ObfuscatedNames.IF_BUTTON8_WRITE3};
        String[][] writeMethods = ObfuscatedNames.IF_BUTTON8_WRITES;
        return new PacketDef(ObfuscatedNames.IF_BUTTON8_OBFUSCATEDNAME, writeData, writeMethods, PacketType.IF_BUTTON);
    }

    public static PacketDef getOpObj5() {
        String[] writeData = new String[]{ObfuscatedNames.OPOBJ5_WRITE1, ObfuscatedNames.OPOBJ5_WRITE2, ObfuscatedNames.OPOBJ5_WRITE3, ObfuscatedNames.OPOBJ5_WRITE4};
        String[][] writeMethods = ObfuscatedNames.OPOBJ5_WRITES;
        return new PacketDef(ObfuscatedNames.OPOBJ5_OBFUSCATEDNAME, writeData, writeMethods, PacketType.OPOBJ);
    }

    public static PacketDef getIfButton5() {
        String[] writeData = new String[]{ObfuscatedNames.IF_BUTTON5_WRITE1, ObfuscatedNames.IF_BUTTON5_WRITE2, ObfuscatedNames.IF_BUTTON5_WRITE3};
        String[][] writeMethods = ObfuscatedNames.IF_BUTTON5_WRITES;
        return new PacketDef(ObfuscatedNames.IF_BUTTON5_OBFUSCATEDNAME, writeData, writeMethods, PacketType.IF_BUTTON);
    }

    public static PacketDef getOpObj4() {
        String[] writeData = new String[]{ObfuscatedNames.OPOBJ4_WRITE1, ObfuscatedNames.OPOBJ4_WRITE2, ObfuscatedNames.OPOBJ4_WRITE3, ObfuscatedNames.OPOBJ4_WRITE4};
        String[][] writeMethods = ObfuscatedNames.OPOBJ4_WRITES;
        return new PacketDef(ObfuscatedNames.OPOBJ4_OBFUSCATEDNAME, writeData, writeMethods, PacketType.OPOBJ);
    }

    public static PacketDef getIfButton4() {
        String[] writeData = new String[]{ObfuscatedNames.IF_BUTTON4_WRITE1, ObfuscatedNames.IF_BUTTON4_WRITE2, ObfuscatedNames.IF_BUTTON4_WRITE3};
        String[][] writeMethods = ObfuscatedNames.IF_BUTTON4_WRITES;
        return new PacketDef(ObfuscatedNames.IF_BUTTON4_OBFUSCATEDNAME, writeData, writeMethods, PacketType.IF_BUTTON);
    }

    public static PacketDef getOpObj3() {
        String[] writeData = new String[]{ObfuscatedNames.OPOBJ3_WRITE1, ObfuscatedNames.OPOBJ3_WRITE2, ObfuscatedNames.OPOBJ3_WRITE3, ObfuscatedNames.OPOBJ3_WRITE4};
        String[][] writeMethods = ObfuscatedNames.OPOBJ3_WRITES;
        return new PacketDef(ObfuscatedNames.OPOBJ3_OBFUSCATEDNAME, writeData, writeMethods, PacketType.OPOBJ);
    }

    public static PacketDef getIfButton7() {
        String[] writeData = new String[]{ObfuscatedNames.IF_BUTTON7_WRITE1, ObfuscatedNames.IF_BUTTON7_WRITE2, ObfuscatedNames.IF_BUTTON7_WRITE3};
        String[][] writeMethods = ObfuscatedNames.IF_BUTTON7_WRITES;
        return new PacketDef(ObfuscatedNames.IF_BUTTON7_OBFUSCATEDNAME, writeData, writeMethods, PacketType.IF_BUTTON);
    }

    public static PacketDef getOpObj2() {
        String[] writeData = new String[]{ObfuscatedNames.OPOBJ2_WRITE1, ObfuscatedNames.OPOBJ2_WRITE2, ObfuscatedNames.OPOBJ2_WRITE3, ObfuscatedNames.OPOBJ2_WRITE4};
        String[][] writeMethods = ObfuscatedNames.OPOBJ2_WRITES;
        return new PacketDef(ObfuscatedNames.OPOBJ2_OBFUSCATEDNAME, writeData, writeMethods, PacketType.OPOBJ);
    }

    public static PacketDef getIfButton6() {
        String[] writeData = new String[]{ObfuscatedNames.IF_BUTTON6_WRITE1, ObfuscatedNames.IF_BUTTON6_WRITE2, ObfuscatedNames.IF_BUTTON6_WRITE3};
        String[][] writeMethods = ObfuscatedNames.IF_BUTTON6_WRITES;
        return new PacketDef(ObfuscatedNames.IF_BUTTON6_OBFUSCATEDNAME, writeData, writeMethods, PacketType.IF_BUTTON);
    }

    public static PacketDef getOpLocT() {
        String[] writeData = new String[]{ObfuscatedNames.OPLOCT_WRITE1, ObfuscatedNames.OPLOCT_WRITE2, ObfuscatedNames.OPLOCT_WRITE3, ObfuscatedNames.OPLOCT_WRITE4, ObfuscatedNames.OPLOCT_WRITE5, ObfuscatedNames.OPLOCT_WRITE6, ObfuscatedNames.OPLOCT_WRITE7};
        String[][] writeMethods = ObfuscatedNames.OPLOCT_WRITES;
        return new PacketDef(ObfuscatedNames.OPLOCT_OBFUSCATEDNAME, writeData, writeMethods, PacketType.OPLOCT);
    }

    public static PacketDef getOpNpcT() {
        String[] writeData = new String[]{ObfuscatedNames.OPNPCT_WRITE1, ObfuscatedNames.OPNPCT_WRITE2, ObfuscatedNames.OPNPCT_WRITE3,ObfuscatedNames.OPNPCT_WRITE4,ObfuscatedNames.OPNPCT_WRITE5};
        String[][] writeMethods = ObfuscatedNames.OPNPCT_WRITES;
        return new PacketDef(ObfuscatedNames.OPNPCT_OBFUSCATEDNAME, writeData, writeMethods, PacketType.OPNPCT);
    }

    public static PacketDef getOpPlayerT() {
        String[] writeData = new String[]{ObfuscatedNames.OPPLAYERT_WRITE1, ObfuscatedNames.OPPLAYERT_WRITE2, ObfuscatedNames.OPPLAYERT_WRITE3, ObfuscatedNames.OPPLAYERT_WRITE4, ObfuscatedNames.OPPLAYERT_WRITE5};
        String[][] writeMethods = ObfuscatedNames.OPPLAYERT_WRITES;
        return new PacketDef(ObfuscatedNames.OPPLAYERT_OBFUSCATEDNAME, writeData, writeMethods, PacketType.OPPLAYERT);
    }

    public static PacketDef getOpObjT() {
        String[] writeData = new String[]{ObfuscatedNames.OPOBJT_WRITE1, ObfuscatedNames.OPOBJT_WRITE2, ObfuscatedNames.OPOBJT_WRITE3, ObfuscatedNames.OPOBJT_WRITE4, ObfuscatedNames.OPOBJT_WRITE5, ObfuscatedNames.OPOBJT_WRITE6, ObfuscatedNames.OPOBJT_WRITE7};
        String[][] writeMethods = ObfuscatedNames.OPOBJT_WRITES;
        return new PacketDef(ObfuscatedNames.OPOBJT_OBFUSCATEDNAME, writeData, writeMethods, PacketType.OPOBJT);
    }

    public static PacketDef getIfButtonT() {
        String[] writeData = new String[]{ObfuscatedNames.IF_BUTTONT_WRITE1, ObfuscatedNames.IF_BUTTONT_WRITE2, ObfuscatedNames.IF_BUTTONT_WRITE3, ObfuscatedNames.IF_BUTTONT_WRITE4, ObfuscatedNames.IF_BUTTONT_WRITE5, ObfuscatedNames.IF_BUTTONT_WRITE6};
        String[][] writeMethods = ObfuscatedNames.IF_BUTTONT_WRITES;
        return new PacketDef(ObfuscatedNames.IF_BUTTONT_OBFUSCATEDNAME, writeData, writeMethods, PacketType.IF_BUTTONT);
    }

    public static PacketDef getOpNpc2() {
        String[] writeData = new String[]{ObfuscatedNames.OPNPC2_WRITE1, ObfuscatedNames.OPNPC2_WRITE2};
        String[][] writeMethods = ObfuscatedNames.OPNPC2_WRITES;
        return new PacketDef(ObfuscatedNames.OPNPC2_OBFUSCATEDNAME, writeData, writeMethods, PacketType.OPNPC);
    }

    public static PacketDef getOpPlayer6() {
        String[] writeData = new String[]{ObfuscatedNames.OPPLAYER6_WRITE1, ObfuscatedNames.OPPLAYER6_WRITE2};
        String[][] writeMethods = ObfuscatedNames.OPPLAYER6_WRITES;
        return new PacketDef(ObfuscatedNames.OPPLAYER6_OBFUSCATEDNAME, writeData, writeMethods, PacketType.OPPLAYER);
    }

    public static PacketDef getOpNpc3() {
        String[] writeData = new String[]{ObfuscatedNames.OPNPC3_WRITE1, ObfuscatedNames.OPNPC3_WRITE2};
        String[][] writeMethods = ObfuscatedNames.OPNPC3_WRITES;
        return new PacketDef(ObfuscatedNames.OPNPC3_OBFUSCATEDNAME, writeData, writeMethods, PacketType.OPNPC);
    }

    public static PacketDef getOpPlayer7() {
        String[] writeData = new String[]{ObfuscatedNames.OPPLAYER7_WRITE1, ObfuscatedNames.OPPLAYER7_WRITE2};
        String[][] writeMethods = ObfuscatedNames.OPPLAYER7_WRITES;
        return new PacketDef(ObfuscatedNames.OPPLAYER7_OBFUSCATEDNAME, writeData, writeMethods, PacketType.OPPLAYER);
    }

    public static PacketDef getOpLoc2() {
        String[] writeData = new String[]{ObfuscatedNames.OPLOC2_WRITE1, ObfuscatedNames.OPLOC2_WRITE2, ObfuscatedNames.OPLOC2_WRITE3, ObfuscatedNames.OPLOC2_WRITE4};
        String[][] writeMethods = ObfuscatedNames.OPLOC2_WRITES;
        return new PacketDef(ObfuscatedNames.OPLOC2_OBFUSCATEDNAME, writeData, writeMethods, PacketType.OPLOC);
    }

    public static PacketDef getOpPlayer8() {
        String[] writeData = new String[]{ObfuscatedNames.OPPLAYER8_WRITE1, ObfuscatedNames.OPPLAYER8_WRITE2};
        String[][] writeMethods = ObfuscatedNames.OPPLAYER8_WRITES;
        return new PacketDef(ObfuscatedNames.OPPLAYER8_OBFUSCATEDNAME, writeData, writeMethods, PacketType.OPPLAYER);
    }

    public static PacketDef getOpLoc1() {
        String[] writeData = new String[]{ObfuscatedNames.OPLOC1_WRITE1, ObfuscatedNames.OPLOC1_WRITE2, ObfuscatedNames.OPLOC1_WRITE3, ObfuscatedNames.OPLOC1_WRITE4};
        String[][] writeMethods = ObfuscatedNames.OPLOC1_WRITES;
        return new PacketDef(ObfuscatedNames.OPLOC1_OBFUSCATEDNAME, writeData, writeMethods, PacketType.OPLOC);
    }

    public static PacketDef getOpNpc1() {
        String[] writeData = new String[]{ObfuscatedNames.OPNPC1_WRITE1, ObfuscatedNames.OPNPC1_WRITE2};
        String[][] writeMethods = ObfuscatedNames.OPNPC1_WRITES;
        return new PacketDef(ObfuscatedNames.OPNPC1_OBFUSCATEDNAME, writeData, writeMethods, PacketType.OPNPC);
    }

    public static PacketDef getOpLoc4() {
        String[] writeData = new String[]{ObfuscatedNames.OPLOC4_WRITE1, ObfuscatedNames.OPLOC4_WRITE2, ObfuscatedNames.OPLOC4_WRITE3, ObfuscatedNames.OPLOC4_WRITE4};
        String[][] writeMethods = ObfuscatedNames.OPLOC4_WRITES;
        return new PacketDef(ObfuscatedNames.OPLOC4_OBFUSCATEDNAME, writeData, writeMethods, PacketType.OPLOC);
    }

    public static PacketDef getOpPlayer2() {
        String[] writeData = new String[]{ObfuscatedNames.OPPLAYER2_WRITE1, ObfuscatedNames.OPPLAYER2_WRITE2};
        String[][] writeMethods = ObfuscatedNames.OPPLAYER2_WRITES;
        return new PacketDef(ObfuscatedNames.OPPLAYER2_OBFUSCATEDNAME, writeData, writeMethods, PacketType.OPPLAYER);
    }

    public static PacketDef getOpLoc3() {
        String[] writeData = new String[]{ObfuscatedNames.OPLOC3_WRITE1, ObfuscatedNames.OPLOC3_WRITE2, ObfuscatedNames.OPLOC3_WRITE3, ObfuscatedNames.OPLOC3_WRITE4};
        String[][] writeMethods = ObfuscatedNames.OPLOC3_WRITES;
        return new PacketDef(ObfuscatedNames.OPLOC3_OBFUSCATEDNAME, writeData, writeMethods, PacketType.OPLOC);
    }

    public static PacketDef getOpPlayer3() {
        String[] writeData = new String[]{ObfuscatedNames.OPPLAYER3_WRITE1, ObfuscatedNames.OPPLAYER3_WRITE2};
        String[][] writeMethods = ObfuscatedNames.OPPLAYER3_WRITES;
        return new PacketDef(ObfuscatedNames.OPPLAYER3_OBFUSCATEDNAME, writeData, writeMethods, PacketType.OPPLAYER);
    }

    public static PacketDef getOpNpc4() {
        String[] writeData = new String[]{ObfuscatedNames.OPNPC4_WRITE1, ObfuscatedNames.OPNPC4_WRITE2};
        String[][] writeMethods = ObfuscatedNames.OPNPC4_WRITES;
        return new PacketDef(ObfuscatedNames.OPNPC4_OBFUSCATEDNAME, writeData, writeMethods, PacketType.OPNPC);
    }

    public static PacketDef getOpPlayer4() {
        String[] writeData = new String[]{ObfuscatedNames.OPPLAYER4_WRITE1, ObfuscatedNames.OPPLAYER4_WRITE2};
        String[][] writeMethods = ObfuscatedNames.OPPLAYER4_WRITES;
        return new PacketDef(ObfuscatedNames.OPPLAYER4_OBFUSCATEDNAME, writeData, writeMethods, PacketType.OPPLAYER);
    }

    public static PacketDef getOpNpc5() {
        String[] writeData = new String[]{ObfuscatedNames.OPNPC5_WRITE1, ObfuscatedNames.OPNPC5_WRITE2};
        String[][] writeMethods = ObfuscatedNames.OPNPC5_WRITES;
        return new PacketDef(ObfuscatedNames.OPNPC5_OBFUSCATEDNAME, writeData, writeMethods, PacketType.OPNPC);
    }

    public static PacketDef getOpPlayer5() {
        String[] writeData = new String[]{ObfuscatedNames.OPPLAYER5_WRITE1, ObfuscatedNames.OPPLAYER5_WRITE2};
        String[][] writeMethods = ObfuscatedNames.OPPLAYER5_WRITES;
        return new PacketDef(ObfuscatedNames.OPPLAYER5_OBFUSCATEDNAME, writeData, writeMethods, PacketType.OPPLAYER);
    }

    public static PacketDef getOpLoc5() {
        String[] writeData = new String[]{ObfuscatedNames.OPLOC5_WRITE1, ObfuscatedNames.OPLOC5_WRITE2, ObfuscatedNames.OPLOC5_WRITE3, ObfuscatedNames.OPLOC5_WRITE4};
        String[][] writeMethods = ObfuscatedNames.OPLOC5_WRITES;
        return new PacketDef(ObfuscatedNames.OPLOC5_OBFUSCATEDNAME, writeData, writeMethods, PacketType.OPLOC);
    }

    public static PacketDef getOpPlayer1() {
        String[] writeData = new String[]{ObfuscatedNames.OPPLAYER1_WRITE1, ObfuscatedNames.OPPLAYER1_WRITE2};
        String[][] writeMethods = ObfuscatedNames.OPPLAYER1_WRITES;
        return new PacketDef(ObfuscatedNames.OPPLAYER1_OBFUSCATEDNAME, writeData, writeMethods, PacketType.OPPLAYER);
    }

    public static PacketDef getMoveGameClick() {
        String[] writeData = new String[]{ObfuscatedNames.MOVE_GAMECLICK_WRITE1, ObfuscatedNames.MOVE_GAMECLICK_WRITE2, ObfuscatedNames.MOVE_GAMECLICK_WRITE3, ObfuscatedNames.MOVE_GAMECLICK_WRITE4};
        String[][] writeMethods = ObfuscatedNames.MOVE_GAMECLICK_WRITES;
        return new PacketDef(ObfuscatedNames.MOVE_GAMECLICK_OBFUSCATEDNAME, writeData, writeMethods, PacketType.MOVE_GAMECLICK);
    }

    public static PacketDef getIfButton1() {
        String[] writeData = new String[]{ObfuscatedNames.IF_BUTTON1_WRITE1, ObfuscatedNames.IF_BUTTON1_WRITE2, ObfuscatedNames.IF_BUTTON1_WRITE3};
        String[][] writeMethods = ObfuscatedNames.IF_BUTTON1_WRITES;
        return new PacketDef(ObfuscatedNames.IF_BUTTON1_OBFUSCATEDNAME, writeData, writeMethods, PacketType.IF_BUTTON);
    }

    public static PacketDef getIfButton3() {
        String[] writeData = new String[]{ObfuscatedNames.IF_BUTTON3_WRITE1, ObfuscatedNames.IF_BUTTON3_WRITE2, ObfuscatedNames.IF_BUTTON3_WRITE3};
        String[][] writeMethods = ObfuscatedNames.IF_BUTTON3_WRITES;
        return new PacketDef(ObfuscatedNames.IF_BUTTON3_OBFUSCATEDNAME, writeData, writeMethods, PacketType.IF_BUTTON);
    }

    public static PacketDef getIfButton2() {
        String[] writeData = new String[]{ObfuscatedNames.IF_BUTTON2_WRITE1, ObfuscatedNames.IF_BUTTON2_WRITE2, ObfuscatedNames.IF_BUTTON2_WRITE3};
        String[][] writeMethods = ObfuscatedNames.IF_BUTTON2_WRITES;
        return new PacketDef(ObfuscatedNames.IF_BUTTON2_OBFUSCATEDNAME, writeData, writeMethods, PacketType.IF_BUTTON);
    }

    public static PacketDef getEventMouseClick() {
        String[] writeData = new String[]{ObfuscatedNames.EVENT_MOUSE_CLICK_WRITE1, ObfuscatedNames.EVENT_MOUSE_CLICK_WRITE2, ObfuscatedNames.EVENT_MOUSE_CLICK_WRITE3};
        String[][] writeMethods = ObfuscatedNames.EVENT_MOUSE_CLICK_WRITES;
        return new PacketDef(ObfuscatedNames.EVENT_MOUSE_CLICK_OBFUSCATEDNAME, writeData, writeMethods, PacketType.EVENT_MOUSE_CLICK);
    }

    public static PacketDef getIfButton10() {
        String[] writeData = new String[]{ObfuscatedNames.IF_BUTTON10_WRITE1, ObfuscatedNames.IF_BUTTON10_WRITE2, ObfuscatedNames.IF_BUTTON10_WRITE3};
        String[][] writeMethods = ObfuscatedNames.IF_BUTTON10_WRITES;
        return new PacketDef(ObfuscatedNames.IF_BUTTON10_OBFUSCATEDNAME, writeData, writeMethods, PacketType.IF_BUTTON);
    }

    public static PacketDef getResumePausebutton() {
        String[] writeData = new String[]{ObfuscatedNames.RESUME_PAUSEBUTTON_WRITE1, ObfuscatedNames.RESUME_PAUSEBUTTON_WRITE2};
        String[][] writeMethods = ObfuscatedNames.RESUME_PAUSEBUTTON_WRITES;
        return new PacketDef(ObfuscatedNames.RESUME_PAUSEBUTTON_OBFUSCATEDNAME, writeData, writeMethods, PacketType.RESUME_PAUSEBUTTON);
    }

}
