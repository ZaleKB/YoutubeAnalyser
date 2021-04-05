package modules;

import com.google.inject.AbstractModule;
import models.GoogleAPI;
import models.GoogleAPIInterface;

/**
 * @author Yuxuan Luan, Rui Li, Junwei Zhang
 */
public class YoutubeAPIModule extends AbstractModule {

  @Override
  protected void configure() {

    bind(GoogleAPIInterface.class).to(GoogleAPI.class);

  }
}
