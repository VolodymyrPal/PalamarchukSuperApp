package com.hfad.palamarchuksuperapp.data.repository

import com.hfad.palamarchuksuperapp.domain.models.MessageAI
import com.hfad.palamarchuksuperapp.domain.models.MessageType
import com.hfad.palamarchuksuperapp.domain.models.Role
import com.hfad.palamarchuksuperapp.domain.models.SubMessageAI
import kotlinx.collections.immutable.persistentListOf

object MockChat {
    val value = listOf(
        MessageAI(
            id = 0,
            role = Role.USER,
            content = persistentListOf(
                SubMessageAI(message = "Hello! Can u help me?")
            )
        ),
        // Model response (image)
        MessageAI(
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
                            "\n "
                ),
                SubMessageAI(message = "Hello! /n /n \n \n How can I help you today?"),
                SubMessageAI(message = "Hello! \n \n \n \n \n \n How can I help you today?")

            )
        ),
        // User message with multiple sub-messages (text)
        MessageAI(
            id = 2,
            role = Role.USER,
            content = persistentListOf(
                SubMessageAI(message = "That looks great! Can you tell me more about it?"),
            )
        ),
        // Model response with additional information (text)
        MessageAI(
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
                            "\n?"
                ),
                SubMessageAI(message = "Hello! /n /n \n \n How can I help you today?"),
                SubMessageAI(message = "Hello! \n \n \n \n \n \n How can I help you today?")
            )
        ),
        // User message with chosen sub-message
        MessageAI(
            id = 4,
            role = Role.USER,
            content = persistentListOf(
                SubMessageAI(message = "This one is perfect, thank you!", isChosen = true),
            )
        ),
        MessageAI(
            id = 5,
            role = Role.MODEL,
            content = persistentListOf(
                SubMessageAI(message = "Hello! How can I help you today?"),
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
                            "unexpected reunions.\n"
                ),
                SubMessageAI(message = "Hello! \n \n \n \n \n \n How can I help you today?")
            )
        ),
        MessageAI(
            id = 6,
            role = Role.USER,
            content = persistentListOf(
                SubMessageAI(message = "Hello! How can I help you today?"),
                SubMessageAI(message = "Hello! /n /n \n \n How can I help you today?"),
                SubMessageAI(message = "Hello! \n \n \n \n \n \n How can I help you today?")
            )
        ),
        MessageAI(
            id = 7,
            role = Role.MODEL,
            content = persistentListOf(
                SubMessageAI(message = "Hello! How can I help you today?"),
                SubMessageAI(message = "Hello! /n /n \n \n How can I help you today?"),
                SubMessageAI(message = "Hello! \n \n \n \n \n \n How can I help you today?")
            )
        ),
        MessageAI(
            id = 8,
            role = Role.USER,
            content = persistentListOf(
                SubMessageAI(message = "Hello! How can I help you today?"),
                SubMessageAI(message = "Hello! /n /n \n \n How can I help you today?"),
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
                            "\n"
                )
            )
        ),
        MessageAI(
            id = 9,
            role = Role.MODEL,
            content = persistentListOf(
                SubMessageAI(message = "Hello! How can I help you today?"),
                SubMessageAI(message = "Hello! /n /n \n \n How can I help you today?"),
                SubMessageAI(message = "Hello! \n \n \n \n \n \n How can I help you today?")
            )
        ),
    )
}