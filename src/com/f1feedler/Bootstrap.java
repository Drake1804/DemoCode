
package com.f1feedler;

import com.backendless.Backendless;
import com.backendless.servercode.IBackendlessBootstrap;

import com.f1feedler.models.Feed;

public class Bootstrap implements IBackendlessBootstrap
{
            
  @Override
  public void onStart()
  {

    Backendless.Persistence.mapTableToClass( "Feed", Feed.class );
    // add your code here
  }
    
  @Override
  public void onStop()
  {
    // add your code here
  }
    
}
        