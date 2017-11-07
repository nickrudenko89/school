package sample;

import java.math.BigInteger;
import java.security.*;

public class Md5
{
    private MessageDigest md;

    public Md5()
    {
        try {
            md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public String crypt(String password)
    {
        md.update(password.getBytes(),0,password.length());
        return new BigInteger(1,md.digest()).toString(16);
    }
}
