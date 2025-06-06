package com.hfad.palamarchuksuperapp.data.repository

import com.hfad.palamarchuksuperapp.data.entities.MessageStatus
import com.hfad.palamarchuksuperapp.domain.models.AiModel
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
            id = 1,
            role = Role.USER,
            content = persistentListOf(
                MessageAI(
                    message = "Hello! Can u help me?",
                    messageGroupId = 0,
                    model = AiModel.OpenAIModel()
                )
            )
        ),
        // Model response (image)
        MessageGroup(
            id = 2,
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
                    messageGroupId = 0,
                    model = AiModel.GeminiModel()
                ),
                MessageAI(
                    message = "Hello! /n /n \n \n How can I help you today?",
                    messageGroupId = 0,
                    model = AiModel.GroqModel()
                ),
                MessageAI(
                    message = "Hello! \n \n \n \n \n \n How can I help you today?",
                    messageGroupId = 0,
                    model = AiModel.OpenAIModel()
                )

            )
        ),
        // User message with multiple sub-messages (text)
        MessageGroup(
            id = 3,
            role = Role.USER,
            content = persistentListOf(
                MessageAI(
                    message = "Hello! That looks great! Can you tell me more about it?",
                    messageGroupId = 0,
                    model = AiModel.GeminiModel()
                ),
            )
        ),
        // Model response with additional information (text)
        MessageGroup(
            id = 4,
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
                    messageGroupId = 0,
                    model = AiModel.GroqModel()
                ),
                MessageAI(
                    message = "Hello! /n /n \n \n How can I help you today?",
                    messageGroupId = 0,
                    isChosen = true,
                    model = AiModel.OpenAIModel()
                ),
                MessageAI(
                    message = "Hello! \n \n \n \n \n \n How can I help you today?",
                    messageGroupId = 0,
                    model = AiModel.GeminiModel()
                ),
            )
        ),
        // User message with chosen sub-message
        MessageGroup(
            id = 5,
            role = Role.USER,
            content = persistentListOf(
                MessageAI(
                    message = "This one is perfect, thank you!",
                    isChosen = true,
                    messageGroupId = 0,
                    model = AiModel.GroqModel()
                ),
            )
        ),
        MessageGroup(
            id = 6,
            role = Role.MODEL,
            content = persistentListOf(
                MessageAI(
                    message = "Hello! How can I help you today?",
                    messageGroupId = 0,
                    model = AiModel.OpenAIModel()
                ),
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
                    messageGroupId = 0,
                    model = AiModel.GeminiModel()
                ),
                MessageAI(
                    message = "Hello! \n \n \n \n \n \n How can I help you today?",
                    messageGroupId = 0,
                    model = AiModel.GroqModel()
                )
            )
        ),
        MessageGroup(
            id = 7,
            role = Role.USER,
            content = persistentListOf(
                MessageAI(
                    message = "Hello! How can I help you today?",
                    messageGroupId = 0,
                    model = AiModel.OpenAIModel()
                ),
                MessageAI(
                    message = "Hello! /n /n \n \n How can I help you today?",
                    messageGroupId = 0,
                    model = AiModel.GeminiModel()
                ),
                MessageAI(
                    message = "Hello! \n \n \n \n \n \n How can I help you today?",
                    messageGroupId = 0,
                    model = AiModel.GroqModel()
                )
            )
        ),
        MessageGroup(
            id = 8,
            role = Role.MODEL,
            content = persistentListOf(
                MessageAI(
                    message = "Hello! How can I help you today?",
                    messageGroupId = 0,
                    model = AiModel.GeminiModel()
                ),
                MessageAI(
                    message = "Hello! /n /n \n \n How can I help you today?",
                    messageGroupId = 0,
                    model = AiModel.GroqModel()
                ),
                MessageAI(
                    message = "Hello! \n \n \n \n \n \n How can I help you today?",
                    messageGroupId = 0,
                    isChosen = true,
                    model = AiModel.OpenAIModel()
                )
            )
        ),
        MessageGroup(
            id = 9,
            role = Role.USER,
            content = persistentListOf(
                MessageAI(
                    message = "Hello! How can I help you today?",
                    messageGroupId = 0,
                    model = AiModel.GroqModel()
                ),
                MessageAI(
                    message = "Hello! /n /n \n \n How can I help you today?",
                    messageGroupId = 0,
                    model = AiModel.OpenAIModel()
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
                            "\n",
                    messageGroupId = 0,
                    model = AiModel.GeminiModel()
                )
            )
        ),
        MessageGroup(
            id = 10,
            role = Role.MODEL,
            content = persistentListOf(
                MessageAI(
                    message = "Hello! How can I help you today?",
                    messageGroupId = 0,
                    model = AiModel.OpenAIModel()
                ),
                MessageAI(
                    message = "Hello! /n /n \n \n How can I help you today?",
                    messageGroupId = 0,
                    model = AiModel.GroqModel()
                ),
                MessageAI(
                    message = "Hello! \n \n \n \n \n \n How can I help you today?",
                    messageGroupId = 0,
                    model = AiModel.GeminiModel(),
                    isChosen = true
                )
            )
        ),
        MessageGroup(
            id = 11,
            role = Role.MODEL,
            content = persistentListOf(
                MessageAI(
                    message = "Some message loading))",
                    status = MessageStatus.LOADING,
                    messageGroupId = 0,
                    model = AiModel.GroqModel()
                ),
            ),
        )
    ).toPersistentList()
}