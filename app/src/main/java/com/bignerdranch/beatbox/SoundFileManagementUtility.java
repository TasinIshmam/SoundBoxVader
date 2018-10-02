package  com.bignerdranch.beatbox;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.SequenceInputStream;
import java.util.List;

/**
 * Created by Tasin Ishmam on 6/8/2018.
 */

public class SoundFileManagementUtility {

    public static final String CACHED_FILES_FODLER = "cached_sounds";

  public static void createSoundChainFile (SoundChain soundChain, Context context, List<Sound>  sounds)  {



      List<Integer> soundChainList = soundChain.getSoundChainList();
      AssetManager assetManager = context.getAssets();

      String filename1 =  sounds.get(soundChainList.get(0)).getAssetPath();

      if(soundChainList.size() == 1) {
          try {
              FileInputStream fistream1 = assetManager.openFd(filename1).createInputStream();
              File outFile = new File(context.getFilesDir().getAbsolutePath(), soundChain.getName() + ".mp3");



              OutputStream out = new FileOutputStream(outFile);

              byte[] buffer = new byte[1024];
              int read;
              while((read = fistream1.read(buffer)) != -1){
                  out.write(buffer, 0, read);
              }

          } catch (IOException e) {
              e.printStackTrace();
              Log.e("TAG","ERROR IN Createsoundchainfile BRUH");

          }

          return;
      }


      String filename2 = sounds.get(soundChainList.get(1)).getAssetPath();

      try {
          FileInputStream fistream1 = assetManager.openFd(filename1).createInputStream();
          FileInputStream fistream2 = assetManager.openFd(filename2).createInputStream();


          File outFile = new File(context.getFilesDir().getAbsolutePath(), soundChain.getName() + ".mp3");
          String tempFileName = soundChain.getName() + "temp.mp3";
          File tempFile = new File(context.getFilesDir().getAbsolutePath(), tempFileName);

          if(!tempFile.exists())
          tempFile.createNewFile();

          outFile = combineTwoMp3(fistream1, fistream2, outFile);



          for (int i = 2; i < soundChainList.size(); i++) {
              copyFile(outFile, tempFile);

              FileInputStream stream1 = new FileInputStream(tempFile);

              String name2 = sounds.get(soundChainList.get(i)).getAssetPath();

              FileInputStream stream2 = assetManager.openFd(name2).createInputStream();

              outFile = combineTwoMp3(stream1, stream2, outFile);
          }
      }
       catch (IOException e) {
          e.printStackTrace();
          Log.e("TAG","ERROR IN Createsoundchainfile BRUH");
      }



  }

  public static File cacheSoundAsset(Sound sound, Context context ) {

      String filename = sound.getAssetPath();
      AssetManager assetManager = context.getAssets();
      File outFolder = new File(context.getFilesDir().getAbsolutePath(), CACHED_FILES_FODLER);

      outFolder.mkdir();

      if(!outFolder.isDirectory()){
          return null;
      }

      String soundFileName = sound.getName() + ".mp3";
      String[] alradyCached = outFolder.list();

      for(String fileName : alradyCached) {
          if(fileName.equals(soundFileName))
              return new File(outFolder, fileName);
      }






      try {
          FileInputStream inputStream = assetManager.openFd(filename).createInputStream();
          File outfile = new File(outFolder, sound.getName() + ".mp3");
          FileOutputStream outputStream = new FileOutputStream(outfile);

          byte[] buffer = new byte[1024];
          int read;

          while((read = inputStream.read(buffer)) != -1){
              outputStream.write(buffer, 0, read);
          }

        return outfile;
      } catch (IOException e) {
          e.printStackTrace();
          return null;
      }


  }

    private static File combineTwoMp3  (FileInputStream fistream1, FileInputStream fistream2, File outFile) {


      try {
          if (!outFile.exists()) {
              outFile.createNewFile();
          }



          SequenceInputStream sistream = new SequenceInputStream(fistream1, fistream2);

          FileOutputStream fostream = new FileOutputStream(outFile);//destinationfile

          int temp;

          while ((temp = sistream.read()) != -1) {
              // System.out.print( (char) temp ); // to print at DOS prompt
              fostream.write(temp);   // to write to file
          }
          fostream.close();
          sistream.close();
          fistream1.close();
          fistream2.close();

      } catch (IOException e) {
          e.printStackTrace();
          Log.e("TAG","ERROR IN COMBINETWOMP3 BRUH");
      }

        return outFile;

    }




    public static void copyAssets(Context context, int assetIndex, List<Sound> sounds) {
        AssetManager assetManager = context.getAssets();




        String filepath = sounds.get(assetIndex).getAssetPath();

        String filename = sounds.get(assetIndex).getName() + ".wav";


        InputStream in = null;
        OutputStream out = null;
        try {
            in = assetManager.open(filepath);
            File outFile = new File(context.getFilesDir(), filename);

            out = new FileOutputStream(outFile);
            //copyFile(in, out);
        } catch (IOException e) {
            Log.e("TAG", "Failed to copy asset file: " + filename, e);
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    // NOOP
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    // NOOP
                }
//            }
            }

        }
    }


    private static void copyFile(File inFile, File outFile) throws IOException {

      InputStream in = new FileInputStream(inFile);
      OutputStream out = new FileOutputStream(outFile);

      byte[] buffer = new byte[1024];
        int read;
        while((read = in.read(buffer)) != -1){
            out.write(buffer, 0, read);
        }
    }


}
