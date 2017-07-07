package com.nguyenquyhy.discordbridge.listeners;

import org.apache.commons.lang3.StringUtils;

import com.nguyenquyhy.discordbridge.DiscordBridge;
import com.nguyenquyhy.discordbridge.models.ChannelConfig;
import com.nguyenquyhy.discordbridge.models.GlobalConfig;
import com.nguyenquyhy.discordbridge.models.TwitterConfig;
import com.nguyenquyhy.discordbridge.utils.ChannelUtil;

import de.btobastian.javacord.entities.Channel;
import twitter4j.FilterQuery;
import twitter4j.StallWarning;
import twitter4j.Status;
import twitter4j.StatusDeletionNotice;
import twitter4j.StatusListener;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.TwitterStream;
import twitter4j.TwitterStreamFactory;
import twitter4j.auth.AccessToken;
import twitter4j.conf.ConfigurationBuilder;

public class TwitterUpdateListener {
    private static DiscordBridge mod = DiscordBridge.getInstance();
    
    public TwitterUpdateListener() throws IllegalStateException, TwitterException{
	GlobalConfig config = mod.getConfig();
	for (ChannelConfig channelConfig : config.channels) {
	    if(channelConfig.twitter != null){
			Channel channel = mod.getBotClient().getChannelById(channelConfig.discordId);
			    TwitterConfig twitterConfig = channelConfig.twitter;
			    if(StringUtils.isNotBlank(twitterConfig.twitterAccessToken) && StringUtils.isNotBlank(twitterConfig.twitterAccessTokenSecret) && StringUtils.isNotBlank(twitterConfig.twitterConsumerKey) && StringUtils.isNotBlank(twitterConfig.twitterConsumerSecret)){
				    TwitterStream twitterStream = new TwitterStreamFactory(new ConfigurationBuilder().setJSONStoreEnabled(true).build()).getInstance();
				    
				    twitterStream.setOAuthConsumer(twitterConfig.twitterConsumerKey,twitterConfig.twitterConsumerSecret);
				    AccessToken token = new AccessToken(twitterConfig.twitterAccessToken, twitterConfig.twitterAccessTokenSecret);
				    twitterStream.setOAuthAccessToken(token);

				    StatusListener listener = new StatusListener() {
				        public void onStatus(Status status) {
				            ChannelUtil.sendMessage(channel,status.getText());
				            
				        }

					@Override
					public void onException(Exception arg0) {
					    // TODO Auto-generated method stub
					    
					}

					@Override
					public void onDeletionNotice(StatusDeletionNotice arg0) {
					    // TODO Auto-generated method stub
					    
					}

					@Override
					public void onScrubGeo(long arg0, long arg1) {
					    // TODO Auto-generated method stub
					    
					}

					@Override
					public void onStallWarning(StallWarning arg0) {
					    // TODO Auto-generated method stub
					    
					}

					@Override
					public void onTrackLimitationNotice(int arg0) {
					    // TODO Auto-generated method stub
					    
					}

				       
				    };

				    twitterStream.addListener(listener);
				    mod.getLogger().info("Registered Twitter Listener");
				    
				    ConfigurationBuilder cb = new ConfigurationBuilder();
				    cb.setDebugEnabled(false)
				      .setOAuthConsumerKey(twitterConfig.twitterConsumerKey)
				      .setOAuthConsumerSecret(twitterConfig.twitterConsumerSecret)
				      .setOAuthAccessToken(twitterConfig.twitterAccessToken)
				      .setOAuthAccessTokenSecret(twitterConfig.twitterAccessTokenSecret);
				    TwitterFactory tf = new TwitterFactory(cb.build());
				    Twitter twitter = tf.getInstance();
				    mod.setTwitterAPI(twitter);
				    
				    FilterQuery query = new FilterQuery();
				    query.follow(twitter.getId());
				    twitterStream.filter(query);
			    }
		    
		    
		}



	}

    }

    

}
