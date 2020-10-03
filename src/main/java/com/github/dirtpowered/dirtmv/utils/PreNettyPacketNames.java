/*
 * Copyright (c) 2020 Dirt Powered
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.github.dirtpowered.dirtmv.utils;

public enum PreNettyPacketNames {
    KEEP_ALIVE(0),
    LOGIN(1),
    HANDSHAKE(2),
    CHAT(3),
    UPDATE_TIME(4),
    ENTITY_EQUIPMENT(5),
    SPAWN_POSITION(6),
    USE_ENTITY(7),
    UPDATE_HEALTH(8),
    RESPAWN(9),
    FLYING(10),
    PLAYER_POSITION(11),
    PLAYER_LOOK(12),
    PLAYER_LOOK_MOVE(13),
    BLOCK_DIG(14),
    BLOCK_PLACE(15),
    BLOCK_ITEM_SWITCH(16),
    SLEEP(17),
    ANIMATION(18),
    ENTITY_ACTION(19),
    NAMED_ENTITY_SPAWN(20),
    ITEM_COLLECT(22),
    VEHICLE_SPAWN(23),
    MOB_SPAWN(24),
    PAINTING_SPAWN(25),
    SPAWN_EXP_ORB(26),
    PLAYER_INPUT(27),
    ENTITY_VELOCITY(28),
    ENTITY_DESTROY(29),
    ENTITY(30),
    ENTITY_RELATIVE_MOVE(31),
    ENTITY_LOOK(32),
    ENTITY_RELATIVE_MOVE_LOOK(33),
    ENTITY_TELEPORT(34),
    ENTITY_HEAD_ROTATION(35),
    ENTITY_STATUS(38),
    ENTITY_ATTACH(39),
    ENTITY_METADATA(40),
    ENTITY_EFFECT(41),
    CLEAR_ENTITY_EFFECT(42),
    SET_EXPERIENCE(43),
    UPDATE_ATTRIBUTES(44),
    MAP_CHUNK(51),
    MULTI_BLOCK_CHANGE(52),
    BLOCK_CHANGE(53),
    PLAY_NOTEBLOCK(54),
    BLOCK_DESTROY(55),
    MAP_CHUNK_BULK(56),
    EXPLOSION(60),
    DOOR_CHANGE(61),
    LEVEL_SOUND(62),
    WORLD_PARTICLE(63),
    GAME_EVENT(70),
    WEATHER(71),
    OPEN_WINDOW(100),
    CLOSE_WINDOW(101),
    WINDOW_CLICK(102),
    SET_SLOT(103),
    WINDOW_ITEMS(104),
    UPDATE_PROGRESSBAR(105),
    INVENTORY_TRANSACTION(106),
    CREATIVE_SET_SLOT(107),
    ENCHANT_ITEM(108),
    UPDATE_SIGN(130),
    MAP_DATA(131),
    UPDATE_TILE_ENTITY(132),
    OPEN_TILE_EDITOR(133),
    STATISTIC(200),
    PLAYER_INFO(201),
    PLAYER_ABILITIES(202),
    TAB_AUTOCOMPLETE(203),
    CLIENT_SETTING(204),
    CLIENT_COMMAND(205),
    SET_OBJECTIVE(206),
    SET_SCORE(207),
    SET_DISPLAY_OBJECTIVE(208),
    SET_TEAM(209),
    CUSTOM_PAYLOAD(250),
    CLIENT_SHARED_KEY(252),
    SERVER_AUTH_DATA(253),
    PING_REQUEST(254),
    KICK_DISCONNECT(255);

    private int packetId;

    PreNettyPacketNames(int i) {
        this.packetId = i;
    }

    public static String getPacketName(int packetId) {
        for (PreNettyPacketNames packetName : values()) {
            if (packetName.packetId == packetId) {
                return packetName.name();
            }
        }

        return "UNKNOWN";
    }
}
