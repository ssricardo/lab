> Notes / draft

# Pre flight

* Take a heap dump file from the desired Tomcat instance

## Refs

* https://www.waitingforcode.com/tomcat/session-storage-in-tomcat/read

## Steps

* Use Eclipse MAT to view/analyse the heap

  * On Histogram Look up for: *org.apache.catalina.session.StandardSession*
  * List objects with outgoing

* For each session entry:
  * expand *attributes -> table -> val* 
  * On the *val*, right click and Calculate Precise Retained Size
    * Here you get how much memory this object retains with its aggregations
 
