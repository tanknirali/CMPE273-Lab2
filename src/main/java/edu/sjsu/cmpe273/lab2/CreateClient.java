package edu.sjsu.cmpe273.lab2;

import io.grpc.ChannelImpl;
import io.grpc.transport.netty.NegotiationType;
import io.grpc.transport.netty.NettyChannelBuilder;

import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A simple client that requests a greeting from the {@link HelloWorldServer}.
 */
public class CreateClient {
  private static final Logger logger = Logger.getLogger(CreateClient.class.getName());

  private final ChannelImpl channel;
  private final PollServiceGrpc.PollServiceBlockingStub blockingStub;

//  private final PollServiceBlockingStub blockingStub;

  public CreateClient(String host, int port) {
    channel =
        NettyChannelBuilder.forAddress(host, port).negotiationType(NegotiationType.PLAINTEXT)
            .build();
    blockingStub = PollServiceGrpc.newBlockingStub(channel);
  }

  public void shutdown() throws InterruptedException {
    channel.shutdown().awaitTerminated(5, TimeUnit.SECONDS);
  }

  public void createPoll(String moderatorId, String question, String started_at, String expired_at, String[] choices) {
    try {
        if(choices.length!=2){
        logger.info("Number of choices must be 2");
        logger.info("There are currently " + choices.length + " choices");
        return;
        }
        else{
       // logger.info("Creating Poll ...");
       
       PollRequest request = PollRequest.newBuilder().setModeratorId(moderatorId).setQuestion(question).setStartedAt(started_at).setExpiredAt(expired_at).build();
      PollResponse response = blockingStub.createPoll(request);
      logger.info("Created a new poll with Id:" + response.getId());
        logger.info("Moderator Id: " + request.getModeratorId() );
        logger.info("Question: " + request.getQuestion() );
        logger.info("Started At: " + request.getStartedAt() );
        logger.info("Expired At: " + request.getExpiredAt() );
        logger.info("Choices[0] " + choices[0] );
        logger.info("Choices[1] " + choices[1] );
//      logger.info("Choices: " + request.getChoice() );
      logger.info("PollId: " + response.getId());
        }
    } catch (RuntimeException e) {
      logger.log(Level.WARNING, "RPC failed", e);
      return;
    }
  }

  public static void main(String[] args) throws Exception {
    CreateClient client = new CreateClient("localhost", 50051);
    try {
      /* Access a service running on the local machine on port 50051 */
      String moderatorId = "1";
        String question = "Which Phone are you using";
        String started_at = "2015-02-23T13:00:00.000Z";
        String expired_at = "2015-02-24T13:00:00.000Z";
        String[] choice = new String[] {"Android", "iPhone"};
      if (args.length > 0) {
        moderatorId  = args[0]; /* Use the arg as the name to greet if provided */
        question = args[1];
        started_at = args[2];
        expired_at = args[3];
//      for(int i=4; i< args.length; i++)
//              choice[i-4] = args[i];
     }
      client.createPoll(moderatorId, question, started_at, expired_at, choice);
    } finally {
      client.shutdown();
    }
  }
}
                 

