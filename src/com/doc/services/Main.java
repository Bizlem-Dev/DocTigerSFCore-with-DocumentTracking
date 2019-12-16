package com.doc.services;

//import numbertoenglishword.Solution;

public class Main {

    /**
     * Provides a simple example on how to use.
     * @param args
     */
    public static void main(String[] args) {

        ArabicTools arabicTools = new ArabicTools();
        //Solution sol=new Solution();
        NumberToWord sol = new NumberToWord();
        arabicTools.isFeminine = true;
        System.out.println(arabicTools.numberToArabicWords("314"));
        System.out.println(arabicTools.numberToArabicWords("200000"));
        System.out.println(arabicTools.numberToArabicWords("696464416455312089898469"));
        
        String englishData=sol.convert(314);
        System.out.println(englishData);
        
        String englishData2=sol.convert(200000);
        System.out.println(englishData2);
		   
        String englishData3=sol.convert(696464413);
        System.out.println(englishData3);
		   
    }
}
