/*
{
  "qlOptions": QLOptions.builder().allowAccessPrivateMethod(true)
}
*/
import com.alibaba.qlexpress4.test.property.SampleForPrivate;

SampleForPrivate a = new SampleForPrivate(5);
assert(a.count == 5);