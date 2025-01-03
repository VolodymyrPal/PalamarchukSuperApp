package com.hfad.palamarchuksuperapp.data.repository

import com.hfad.palamarchuksuperapp.domain.models.MessageGroup
import com.hfad.palamarchuksuperapp.domain.models.MessageType
import com.hfad.palamarchuksuperapp.domain.models.Role
import com.hfad.palamarchuksuperapp.domain.models.MessageAI
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

object MockChat {
    operator fun invoke() : PersistentList<MessageGroup> = listOf(
        MessageGroup(
            id = 0,
            role = Role.USER,
            content = persistentListOf(
                SubMessageAI(message = "Hello! Can u help me?", messageAiID = 0)
            )
        ),
        // Model response (image)
        MessageGroup(
            id = 1,
            role = Role.MODEL,
            type = MessageType.TEXT,
            content = persistentListOf(
                SubMessageAI(
                    message = "Hello! Once upon a time, in a quaint little house, lived a pair of socks," +
                            " inseparable companions. They were always together, whether it was snuggled " +
                            "in the drawer or adventuring on a foot.\n" +
                            "\n" +
                            "One fateful day, a mischievous dryer decided to play a trick. " +
                            "It separated the pair, leaving one sock alone and bewildered. " +
                            "The lone sock searched high and low, calling out for its partner. " +
                            "It rummaged through the laundry basket, peeked under the bed, " +
                            "and even asked the wind to help.\n" +
                            "\n" +
                            "In the end, it was a kind and thoughtful human who found the lost sock. " +
                            "They reunited the pair, and the two socks were overjoyed. " +
                            "They learned a valuable lesson that day: even in the darkest of times, " +
                            "hope and kindness can lead to unexpected reunions.\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n ",
                    messageAiID = 1
                ),
                SubMessageAI(
                    message = "Hello! /n /n \n \n How can I help you today?",
                    messageAiID = 1
                ),
                SubMessageAI(
                    message = "Hello! \n \n \n \n \n \n How can I help you today?",
                    messageAiID = 1
                )

            )
        ),
        // User message with multiple sub-messages (text)
        MessageGroup(
            id = 2,
            role = Role.USER,
            content = persistentListOf(
                SubMessageAI(
                    message = "That looks great! Can you tell me more about it?",
                    messageAiID = 2
                ),
            )
        ),
        // Model response with additional information (text)
        MessageGroup(
            id = 3,
            role = Role.MODEL,
            content = persistentListOf(
                SubMessageAI(
                    message = "Hello! Once upon a time, in a quaint little house, lived a pair of socks, " +
                            "inseparable companions. They were always together, whether it was" +
                            " snuggled in the drawer or adventuring on a foot.\n" +
                            "\n" +
                            "One fateful day, a mischievous dryer decided to play a trick. It " +
                            "separated the pair, leaving one sock alone and bewildered. The lone sock " +
                            "searched high and low, calling out for its partner. It rummaged through the" +
                            " laundry basket, peeked under the bed, and even asked the wind to help.\n" +
                            "\n" +
                            "In the end, it was a kind and thoughtful human who found the lost sock. " +
                            "They reunited the pair, and the two socks were overjoyed. They learned a " +
                            "valuable lesson that day: even in the darkest of times, hope and kindness " +
                            "can lead to unexpected reunions.\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n?",
                    messageAiID = 3
                ),
                SubMessageAI(
                    message = "Hello! /n /n \n \n How can I help you today?",
                    messageAiID = 3
                ),
                SubMessageAI(
                    message = "Hello! \n \n \n \n \n \n How can I help you today?",
                    messageAiID = 3
                )
            )
        ),
        // User message with chosen sub-message
        MessageGroup(
            id = 4,
            role = Role.USER,
            content = persistentListOf(
                SubMessageAI(
                    message = "This one is perfect, thank you!",
                    isChosen = true,
                    messageAiID = 4
                ),
            )
        ),
        MessageGroup(
            id = 5,
            role = Role.MODEL,
            content = persistentListOf(
                SubMessageAI(message = "Hello! How can I help you today?", messageAiID = 5),
                SubMessageAI(
                    message = "Hello! Once upon a time, in a quaint little house, lived a pair of socks, " +
                            "inseparable companions. They were always together, whether it was snuggled " +
                            "in the drawer or adventuring on a foot.\n" +
                            "\n" +
                            "One fateful day, a mischievous dryer decided to play a trick. It separated " +
                            "the pair, leaving one sock alone and bewildered. The lone sock searched high " +
                            "and low, calling out for its partner. It rummaged through the laundry basket, " +
                            "peeked under the bed, and even asked the wind to help.\n" +
                            "\n" +
                            "In the end, it was a kind and thoughtful human who found the lost sock. They " +
                            "reunited the pair, and the two socks were overjoyed. They learned a valuable " +
                            "lesson that day: even in the darkest of times, hope and kindness can lead to " +
                            "unexpected reunions.\n",
                    messageAiID = 5
                ),
                SubMessageAI(
                    message = "Hello! \n \n \n \n \n \n How can I help you today?",
                    messageAiID = 5
                )
            )
        ),
        MessageGroup(
            id = 6,
            role = Role.USER,
            content = persistentListOf(
                SubMessageAI(message = "Hello! How can I help you today?", messageAiID = 6),
                SubMessageAI(
                    message = "Hello! /n /n \n \n How can I help you today?",
                    messageAiID = 6
                ),
                SubMessageAI(
                    message = "Hello! \n \n \n \n \n \n How can I help you today?",
                    messageAiID = 6
                )
            )
        ),
        MessageGroup(
            id = 7,
            role = Role.MODEL,
            content = persistentListOf(
                SubMessageAI(message = "Hello! How can I help you today?", messageAiID = 7),
                SubMessageAI(
                    message = "Hello! /n /n \n \n How can I help you today?",
                    messageAiID = 7
                ),
                SubMessageAI(
                    message = "Hello! \n \n \n \n \n \n How can I help you today?",
                    messageAiID = 7
                )
            )
        ),
        MessageGroup(
            id = 8,
            role = Role.USER,
            content = persistentListOf(
                SubMessageAI(message = "Hello! How can I help you today?", messageAiID = 8),
                SubMessageAI(
                    message = "Hello! /n /n \n \n How can I help you today?",
                    messageAiID = 8
                ),
                SubMessageAI(
                    message = "Hello! Once upon a time, in a quaint little house, lived a pair of " +
                            "socks, inseparable companions. They were always together, whether it was " +
                            "snuggled in the drawer or adventuring on a foot.\n" +
                            "\n" +
                            "One fateful day, a mischievous dryer decided to play a trick. It separated " +
                            "the pair, leaving one sock alone and bewildered. The lone sock searched " +
                            "high and low, calling out for its partner. It rummaged through the laundry " +
                            "basket, peeked under the bed, and even asked the wind to help.\n" +
                            "\n" +
                            "In the end, it was a kind and thoughtful human who found the lost sock. " +
                            "They reunited the pair, and the two socks were overjoyed. They learned a " +
                            "valuable lesson that day: even in the darkest of times, hope and kindness " +
                            "can lead to unexpected reunions.\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n" +
                            "\n", messageAiID = 8
                )
            )
        ),
        MessageGroup(
            id = 9,
            role = Role.MODEL,
            content = persistentListOf(
                SubMessageAI(message = "Hello! How can I help you today?", messageAiID = 9),
                SubMessageAI(
                    message = "Hello! /n /n \n \n How can I help you today?",
                    messageAiID = 9
                ),
                SubMessageAI(
                    message = "Hello! \n \n \n \n \n \n How can I help you today?",
                    messageAiID = 9
                )
            )
        ),
        MessageGroup(
            id = 10,
            role = Role.MODEL,
            content = persistentListOf(
                SubMessageAI(message = "", loading = true, messageAiID = 10)
            )
        )
    ).toPersistentList()
}