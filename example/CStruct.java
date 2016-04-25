//http://www.thecodingforums.com/threads/how-to-convert-c-struct-to-java-classes.709623/

import dk.vajhoej.record.RecordException;
import dk.vajhoej.record.StructReader;

public class CStruct
{
	public static void main(String[] args) throws RecordException
	{
		byte[] b = { 7, 0, 0, 0, 0x12, 0, 3, 0 };
		StructReader sr = new StructReader(b);
		Data o = sr.read(Data.class);
		System.out.println(o.getLiv() + " " + o.getBv1() + " " +
		o.getBv2() + " " + o.getSiv());
		//output is 7 1 2 3
	}
}
//EOF
