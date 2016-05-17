package com.f1feedler.models;

import com.backendless.Backendless;
import com.backendless.BackendlessUser;
import com.backendless.geo.GeoPoint;

public class Feed
{
  private java.util.Date created;
  private String title;
  private String description;
  private java.util.Date updated;
  private String creatingDate;
  private String objectId;
  private String descrioption;
  private String link;
  private String ownerId;
  private String image;

  public java.util.Date getCreated()
  {
    return this.created;
  }

  public String getTitle()
  {
    return this.title;
  }

  public String getDescription()
  {
    return this.description;
  }

  public java.util.Date getUpdated()
  {
    return this.updated;
  }

  public String getCreatingDate()
  {
    return this.creatingDate;
  }

  public String getObjectId()
  {
    return this.objectId;
  }

  public String getDescrioption()
  {
    return this.descrioption;
  }

  public String getLink()
  {
    return this.link;
  }

  public String getOwnerId()
  {
    return this.ownerId;
  }

  public String getImage()
  {
    return this.image;
  }


  public void setCreated( java.util.Date created )
  {
    this.created = created;
  }

  public void setTitle( String title )
  {
    this.title = title;
  }

  public void setDescription( String description )
  {
    this.description = description;
  }

  public void setUpdated( java.util.Date updated )
  {
    this.updated = updated;
  }

  public void setCreatingDate( String creatingDate )
  {
    this.creatingDate = creatingDate;
  }

  public void setObjectId( String objectId )
  {
    this.objectId = objectId;
  }

  public void setDescrioption( String descrioption )
  {
    this.descrioption = descrioption;
  }

  public void setLink( String link )
  {
    this.link = link;
  }

  public void setOwnerId( String ownerId )
  {
    this.ownerId = ownerId;
  }

  public void setImage( String image )
  {
    this.image = image;
  }

  public Feed save()
  {
    return Backendless.Data.of( Feed.class ).save( this );
  }

  public Long remove()
  {
    return Backendless.Data.of( Feed.class ).remove( this );
  }

  public static Feed findById( String id )
  {
    return Backendless.Data.of( Feed.class ).findById( id );
  }

  public static Feed findFirst()
  {
    return Backendless.Data.of( Feed.class ).findFirst();
  }

  public static Feed findLast()
  {
    return Backendless.Data.of( Feed.class ).findLast();
  }
}