package ExV;

/**
  An interface for sorting
  To use: implement it by defining length, compare, and exchange functions
  like in
@see SortString
@see SortDouble
@see SortInt

<br> to sort use
<pre>
MySortable implements Sortable{
public int length () {...}
public int compare (int i, int j) {...}
public void exchange (int i, int j) {...}
}

main ()
{
   MySortable a (..);
   int order [] = QSort .order (a);// Returns order
   QSort .sort (a);                // Sorts inline
}
</pre>

<br> To sort int, double, or string array use:
<pre>
int i [];
double d [];
String s [];
QSort .sort (i);
QSort .sort (d);
QSort .sort (s);
</pre>
@version        Wed Aug 28 17:58:27 1996
@author         A. Mockus
*/
public interface Sortable {
   int length ();
   int compare (int i, int j);
   void exchange (int left, int right);
};





