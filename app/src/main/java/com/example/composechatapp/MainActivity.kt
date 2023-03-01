package com.example.composechatapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.composechatapp.ui.theme.ComposeChatAppTheme
import com.pubnub.api.PNConfiguration
import com.pubnub.api.PubNub
import com.pubnub.api.UserId
import com.pubnub.api.enums.PNLogVerbosity
import com.pubnub.components.chat.provider.LocalMemberRepository
import com.pubnub.components.chat.provider.LocalMembershipRepository
import com.pubnub.components.chat.ui.component.provider.LocalChannel
import com.pubnub.components.chat.viewmodel.message.MessageViewModel
import com.pubnub.components.data.member.DBMember
import com.pubnub.components.data.membership.DBMembership
import com.pubnub.components.repository.member.DefaultMemberRepository
import com.pubnub.components.repository.membership.DefaultMembershipRepository
import com.pubnub.framework.data.ChannelId
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    private lateinit var pubNub: PubNub
    private var channelId = "default-article"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initializePubNub()
        setContent {
            ComposeChatAppTheme(pubNub = pubNub) {
                AddDummyData(channelId = arrayOf(channelId))
                Box(modifier = Modifier.fillMaxSize()) {
                    ChannelView(channelId = channelId)
                }
            }
        }
    }

    private fun initializePubNub() {
        pubNub = PubNub(PNConfiguration(userId = UserId(value = getRandomString(6))).apply {
            publishKey = "public_key_here"
            subscribeKey = "subscribe_key_here"
            logVerbosity = PNLogVerbosity.BODY
        })
    }

    private fun destroyPubNub() {
        pubNub.destroy()
    }


    private fun getRandomString(length: Int): String {
        val allowedChars = ('A'..'Z') + ('a'..'z') + ('0'..'9')
        return (1..length).map { allowedChars.random() }.joinToString("")
    }

    @Composable
    fun ChannelView(channelId: ChannelId) {
        // region Content data
        val messageViewModel: MessageViewModel = MessageViewModel.defaultWithMediator()
        val messages = remember { messageViewModel.getAll(channelId = channelId) }
        // endregion

        CompositionLocalProvider( LocalChannel provides channelId) {
            Chat.Content(messages = messages)
        }
    }

    @Composable
    fun AddDummyData(vararg channelId: ChannelId) {

        // Creates a user object with uuid
        val memberRepository: DefaultMemberRepository = LocalMemberRepository.current as DefaultMemberRepository
        val member: DBMember = DBMember(id = pubNub.configuration.userId.toString(),
            name = pubNub.configuration.userId.toString(),
            profileUrl = "https://picsum.photos/seed/${pubNub.configuration.userId.toString()}/200")

        // Creates a membership so that the user could subscribe to channels
        val membershipRepository: DefaultMembershipRepository = LocalMembershipRepository.current as DefaultMembershipRepository
        val memberships: Array<DBMembership> = channelId.map { id -> DBMembership(channelId = id, memberId = member.id) }.toTypedArray()

        // Fills the database with member and memberships data
        val scope = rememberCoroutineScope()
        LaunchedEffect(null) {
            scope.launch {
                memberRepository.insertOrUpdate(member)
                membershipRepository.insertOrUpdate(*memberships)
            }
        }
    }
}