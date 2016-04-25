//from http://www.thecodingforums.com/threads/how-to-convert-c-struct-to-java-classes.709623/

import dk.vajhoej.record.Alignment;
import dk.vajhoej.record.Endian;
import dk.vajhoej.record.FieldType;
import dk.vajhoej.record.Struct;
import dk.vajhoej.record.StructField;

/*
* struct data
* {
*   long int   liv;
*   int        bv1 : 4;
*   int        bv2 : 4;
*   short int  siv;
* };
*
* With a compiler and settings that uses little endian, natural
* alignment, sizeof(long)=4, sizeof(short)=2 etc..
*/
@Struct(endianess=Endian.LITTLE, alignment=Alignment.NATURAL)
public class Data
{
	@StructField(n=0,type=FieldType.INT4)
	private int liv;
	@StructField(n=1,type=FieldType.BIT,length=4)
	private int bv1;
	@StructField(n=2,type=FieldType.BIT,length=4)
	private int bv2;
	@StructField(n=3,type=FieldType.INT2)
	private int siv;
	public int getLiv()
	{
		return liv;
	}
	public int getBv1()
	{
		return bv1;
	}
	public int getBv2()
	{
		return bv2;
	}
	public int getSiv()
	{
		return siv;
	}
}
//EOF
