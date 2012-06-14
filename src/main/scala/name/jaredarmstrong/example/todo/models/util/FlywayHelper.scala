package name.jaredarmstrong.example.todo.models.util

/**
 * Created with IntelliJ IDEA.
 * User: jarmstrong
 * Date: 6/9/12
 * Time: 7:44 PM
 * To change this template use File | Settings | File Templates.
 */

import java.sql._
import javax.sql._
import com.googlecode.flyway.core.Flyway
import com.googlecode.flyway.core.metadatatable.MetaDataTableRow


import name.jaredarmstrong.example.todo.common.{AppDb,Loggable}


class FlywayHelper extends Loggable {

  def migrate(): Int = {

    val dataSource: DataSource = AppDb.getDataSource;
    val flyway: Flyway = new Flyway();


    flyway.setDataSource(dataSource);
    flyway.setBasePackage("com.googlecode.flyway.sample.migration");

    try{
      flyway.init()
    }catch{
      case e => {
        logger.info(e.toString)
      }
    }

    flyway.migrate();

    val successCount: Int = flyway.migrate();

    successCount
  }

  def requiresMigration(target: String): Boolean = {

    val dataSource: DataSource = AppDb.getDataSource;
    val flyway: Flyway = new Flyway();


    flyway.setDataSource(dataSource);
    flyway.setBasePackage("name.jaredarmstrong.example.todo.models.migration");


    val status: MetaDataTableRow = flyway.status()

    if(status != null ){
      logger.info("status at: "+status.getVersion().toString())

      if(status.getVersion().toString() == target){
        false
      }else{
        true
      }
    }  else{
      logger.info("status == null")
      false
    }

  }

  def currentMigrationVer(): String = {
    val dataSource: DataSource = AppDb.getDataSource;
    val flyway: Flyway = new Flyway();

    flyway.setDataSource(dataSource);
    flyway.setBasePackage("name.jaredarmstrong.example.todo.models.migration");

    val status: MetaDataTableRow = flyway.status()

    if(status != null ){
      status.getVersion().toString()
    }  else{
      logger.info("status == null")
      ""
    }

  }

  def history() = {
    val dataSource: DataSource = AppDb.getDataSource;
    val flyway: Flyway = new Flyway();


    flyway.setDataSource(dataSource);
    flyway.setBasePackage("name.jaredarmstrong.example.todo.models.migration");

    flyway.history();
  }

  def clean()  {
    val dataSource: DataSource = AppDb.getDataSource;
    val flyway: Flyway = new Flyway();


    flyway.setDataSource(dataSource);
    flyway.setBasePackage("name.jaredarmstrong.example.todo.models.migration");

    flyway.clean();
  }

}

object FlywayHelper extends FlywayHelper {

}