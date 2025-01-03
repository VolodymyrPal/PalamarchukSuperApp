package com.hfad.palamarchuksuperapp.data.repository

import com.hfad.palamarchuksuperapp.domain.models.MessageGroup
import com.hfad.palamarchuksuperapp.domain.models.MessageType
import com.hfad.palamarchuksuperapp.domain.models.Role
import com.hfad.palamarchuksuperapp.domain.models.MessageAI
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import kotlinx.collections.immutable.toPersistentList

object MockChat {
    operator fun invoke(): PersistentList<MessageGroup> = listOf(
        MessageGroup(
            id = 0,
            role = Role.USER,
            content = persistentListOf(
                MessageAI(message = "Hello! Can u help me?", messageGroupId = 0)
            )
        ),
        // Model response (image)
        MessageGroup(
            id = 1,
            role = Role.MODEL,
            type = MessageType.TEXT,
            content = persistentListOf(
                MessageAI(
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
                    messageGroupId = 1
                ),
                MessageAI(
                    message = "Hello! /n /n \n \n How can I help you today?",
                    messageGroupId = 1
                ),
                MessageAI(
                    message = "Hello! \n \n \n \n \n \n How can I help you today?",
                    messageGroupId = 1
                )

            )
        ),
        // User message with multiple sub-messages (text)
        MessageGroup(
            id = 2,
            role = Role.USER,
            content = persistentListOf(
                MessageAI(
                    message = "That looks great! Can you tell me more about it?",
                    messageGroupId = 2
                ),
            )
        ),
        // Model response with additional information (text)
        MessageGroup(
            id = 3,
            role = Role.MODEL,
            content = persistentListOf(
                MessageAI(
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
                    messageGroupId = 3
                ),
                MessageAI(
                    message = "Hello! /n /n \n \n How can I help you today?",
                    messageGroupId = 3
                ),
                MessageAI(
                    message = "Hello! \n \n \n \n \n \n How can I help you today?",
                    messageGroupId = 3
                )
            )
        ),
        // User message with chosen sub-message
        MessageGroup(
            id = 4,
            role = Role.USER,
            content = persistentListOf(
                MessageAI(
                    message = "This one is perfect, thank you!",
                    isChosen = true,
                    messageGroupId = 4
                ),
            )
        ),
        MessageGroup(
            id = 5,
            role = Role.MODEL,
            content = persistentListOf(
                MessageAI(message = "Hello! How can I help you today?", messageGroupId = 5),
                MessageAI(
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
                    messageGroupId = 5
                ),
                MessageAI(
                    message = "Hello! \n \n \n \n \n \n How can I help you today?",
                    messageGroupId = 5
                )
            )
        ),
        MessageGroup(
            id = 6,
            role = Role.USER,
            content = persistentListOf(
                MessageAI(message = "Hello! How can I help you today?", messageGroupId = 6),
                MessageAI(
                    message = "Hello! /n /n \n \n How can I help you today?",
                    messageGroupId = 6
                ),
                MessageAI(
                    message = "Hello! \n \n \n \n \n \n How can I help you today?",
                    messageGroupId = 6
                )
            )
        ),
        MessageGroup(
            id = 7,
            role = Role.MODEL,
            content = persistentListOf(
                MessageAI(message = "Hello! How can I help you today?", messageGroupId = 7),
                MessageAI(
                    message = "Hello! /n /n \n \n How can I help you today?",
                    messageGroupId = 7
                ),
                MessageAI(
                    message = "Hello! \n \n \n \n \n \n How can I help you today?",
                    messageGroupId = 7
                )
            )
        ),
        MessageGroup(
            id = 8,
            role = Role.USER,
            content = persistentListOf(
                MessageAI(message = "Hello! How can I help you today?", messageGroupId = 8),
                MessageAI(
                    message = "Hello! /n /n \n \n How can I help you today?",
                    messageGroupId = 8
                ),
                MessageAI(
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
                            "\n", messageGroupId = 8
                )
            )
        ),
        MessageGroup(
            id = 9,
            role = Role.MODEL,
            content = persistentListOf(
                MessageAI(message = "Hello! How can I help you today?", messageGroupId = 9),
                MessageAI(
                    message = "Hello! /n /n \n \n How can I help you today?",
                    messageGroupId = 9
                ),
                MessageAI(
                    message = "Hello! \n \n \n \n \n \n How can I help you today?",
                    messageGroupId = 9
                )
            )
        ),
        MessageGroup(
            id = 10,
            role = Role.MODEL,
            content = persistentListOf(
                MessageAI(message = "", loading = true, messageGroupId = 10)
            )
        )
    ).toPersistentList()
}