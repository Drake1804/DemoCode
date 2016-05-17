package com.f1feedler.events.persistence_service;

import com.backendless.BackendlessCollection;
import com.backendless.geo.GeoPoint;
import com.backendless.persistence.BackendlessDataQuery;
import com.backendless.property.ObjectProperty;
import com.backendless.servercode.ExecutionResult;
import com.backendless.servercode.RunnerContext;
import com.backendless.servercode.annotation.Asset;
import com.backendless.servercode.annotation.Async;

import com.f1feedler.models.Feed;

import java.util.HashMap;
import java.util.Map;
        
/**
* FeedTableEventHandler handles events for all entities. This is accomplished
* with the @Asset( "Feed" ) annotation. 
* The methods in the class correspond to the events selected in Backendless
* Console.
*/
    
@Asset( "Feed" )
public class FeedTableEventHandler extends com.backendless.servercode.extension.PersistenceExtender<Feed>
{
    
  @Override
  public void beforeCreate( RunnerContext context, Feed feed) throws Exception
  {
    // add your code here
  }
    
}
        