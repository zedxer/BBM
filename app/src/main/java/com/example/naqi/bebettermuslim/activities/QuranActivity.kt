package com.example.naqi.bebettermuslim.activities

import android.Manifest
import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.annotation.TargetApi
import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Rect
import android.graphics.Typeface
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.AsyncTask
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.Spanned
import android.util.Log
import android.view.*
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import com.example.naqi.bebettermuslim.R
import com.example.naqi.bebettermuslim.Utils.Constants
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.QURAN_AUDIO_PATH
import com.example.naqi.bebettermuslim.Utils.Constants.Companion.QURAN_TRANSLATION_PATH
import com.example.naqi.bebettermuslim.adapter.SurahNameAdapter
import com.example.naqi.bebettermuslim.models.Quran
import com.example.naqi.bebettermuslim.models.Sura
import com.example.naqi.bebettermuslim.models.SurahDataHolder
import com.example.naqi.bebettermuslim.setting_activities.QuranSettings
import com.example.naqi.bebettermuslim.views.*
import com.google.android.gms.analytics.HitBuilders
import com.google.android.gms.analytics.Tracker
import com.gun0912.tedpermission.PermissionListener
import com.gun0912.tedpermission.TedPermission
import com.liulishuo.filedownloader.BaseDownloadTask
import com.liulishuo.filedownloader.FileDownloadListener
import com.liulishuo.filedownloader.FileDownloader
import com.stanfy.gsonxml.GsonXml
import com.stanfy.gsonxml.GsonXmlBuilder
import com.stanfy.gsonxml.XmlParserCreator
import io.github.inflationx.viewpump.ViewPumpContextWrapper
import kotlinx.android.synthetic.main.activity_quran.*
import org.xmlpull.v1.XmlPullParserFactory
import xyz.danoz.recyclerviewfastscroller.vertical.VerticalRecyclerViewFastScroller
import java.io.*
import java.net.HttpURLConnection
import java.net.URL
import java.util.ArrayList

class QuranActivity : AppCompatActivity() {
    private var linearLayoutManager: LinearLayoutManager? = null
    private var allAudioPaths: ArrayList<String>? = null
    private var mediaPlayer: MediaPlayer? = null
    private var clickedCounter: Int = 0
    private var ayaNumber: Int = 0
    private lateinit var baseDir: File

    private var fd: FileDescriptor? = null
    private lateinit var fis: FileInputStream

    private var surahNumber: Int = 0
    internal lateinit var allSpans: ArrayList<ArrayList<MyClickableSpan>>
    private var audioManager: AudioManager? = null
    private var scrollPosition: Int = 0
    private lateinit var gsonXml: GsonXml
    private lateinit var sections: MutableList<SimpleSectionedRecyclerViewAdapter.Section>
    private var ayaSeparatorFont: Typeface? = null
    private var ayaSeparator: String? = null

    private lateinit var quranPreferences: SharedPreferences
    private lateinit var editor: SharedPreferences.Editor
    private lateinit var recyclerView: RecyclerView
    private lateinit var toolbar: Toolbar

    internal lateinit var listener: SharedPreferences.OnSharedPreferenceChangeListener
    private var scrollState: Boolean = false
    private var mTracker: Tracker? = null
    private var reciter: String? = null
    private var totalAya: Int = 0
    private lateinit var fastScroller: VerticalRecyclerViewFastScroller
    private var isPlayClicked = false
    private lateinit var quranArabic: Quran
    private lateinit var surahArabic: Array<Sura>
    var storagePermissionFlag = false

    var permissionlistener = object : PermissionListener {
        override fun onPermissionDenied(deniedPermissions: MutableList<String>?) {
            storagePermissionFlag = false
        }

        override fun onPermissionGranted() {
            storagePermissionFlag = true
        }
        }

    fun resizeDrawable(drawableResource: Int): Drawable {
        val drawable = resources.getDrawable(drawableResource)
        val bitmap = (drawable as BitmapDrawable).bitmap
        return BitmapDrawable(resources, Bitmap.createScaledBitmap(bitmap, 80, 80, true))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_quran)
        baseDir = Environment.getExternalStorageDirectory()

        ayaSeparatorFont = Typeface.createFromAsset(assets, "fonts/Scheherazade-Regular.ttf")
        ayaSeparator = Character.toString(1757.toChar())
        mediaPlayer = MediaPlayer()
        mediaPlayer!!.setOnErrorListener(MediaPlayer.OnErrorListener { mp, what, extra ->
            isPlayClicked = false
            println()
            false
        })

        toolbar = findViewById(R.id.quranToolbar)
        toolbar.setLogo(resizeDrawable(R.drawable.quran_fill))
        setSupportActionBar(toolbar)
        if (supportActionBar != null) {
            supportActionBar!!.setTitle("Quran")
            supportActionBar!!.setDisplayHomeAsUpEnabled(true)
            supportActionBar!!.setHomeButtonEnabled(true)
            supportActionBar!!.setHomeAsUpIndicator(R.drawable.disclosure_left)
        }

        quranPreferences = getSharedPreferences(Constants.QURAN_SETTINGS, MODE_PRIVATE)
        editor = quranPreferences.edit()
        scrollState = quranPreferences.getBoolean(Constants.SCROLL_STATE, true)
        updateScreenState()

        recyclerView = findViewById<View>(R.id.recyclerView) as RecyclerView
        linearLayoutManager = LinearLayoutManager(this)
        recyclerView.setHasFixedSize(true)

        val itemDecoration = DividerItemDecoration(this, DividerItemDecoration.VERTICAL_LIST)
        recyclerView.addItemDecoration(itemDecoration)

        fastScroller = findViewById(R.id.fast_scroller)
        fastScroller.setRecyclerView(recyclerView)
//        recyclerView.setOnScrollListener(fastScroller.onScrollListener)
        recyclerView.addOnScrollListener(fastScroller.onScrollListener)

        reciter = quranPreferences.getString(Constants.RECITER, "AbdulRahman")

        val parserCreator = XmlParserCreator {
            try {
                return@XmlParserCreator XmlPullParserFactory.newInstance().newPullParser()
            } catch (e: Exception) {
                throw RuntimeException(e)
            }
        }

        gsonXml = GsonXmlBuilder()
            .setXmlParserCreator(parserCreator).setSameNameLists(true)
            .create()

        recyclerView.layoutManager = linearLayoutManager

        listener = SharedPreferences.OnSharedPreferenceChangeListener { prefSettings, key ->
            Log.d("PREFERENCE STATE", "Changed")

            if (key == Constants.SCREEN_STATE) {
                updateScreenState()
            }
            scrollState = prefSettings.getBoolean(Constants.SCROLL_STATE, true)

            if (key == Constants.RECITER) {
                reciter = prefSettings.getString(Constants.RECITER, "AbdulRahman")
            }
        }

        quranPreferences.registerOnSharedPreferenceChangeListener(listener)
        TedPermission.with(this)
            .setPermissionListener(permissionlistener)
            .setDeniedMessage("If you reject permission,you can not use this service\n\nPlease turn on permissions at [Setting] > [Permission]")
            .setPermissions(Manifest.permission.WRITE_EXTERNAL_STORAGE)
            .check();


        soundOptionsMenu.setOnTouchListener(object : OnSwipeTouchListener(this@QuranActivity) {
            override fun onSwipeBottom() {
                //                openSoundOptions.setVisibility(View.VISIBLE);

                soundOptionsMenu.animate()
                    .translationY(soundOptionsMenu.getHeight().toFloat())
                    .setListener(object : AnimatorListenerAdapter() {
                        override fun onAnimationEnd(animation: Animator) {
                            openSoundOptions.animate().translationY(0f)
                            println("Animation End")
                            super.onAnimationEnd(animation)
                        }
                    })
            }
        })

        openSoundOptions.setOnTouchListener(object : OnSwipeTouchListener(this@QuranActivity) {

            override fun onSwipeTop() {

                soundOptionsMenu.setVisibility(View.VISIBLE)
                //                openSoundOptions.setVisibility(View.GONE);
                openSoundOptions.animate()
                    .translationY(openSoundOptions.getHeight().toFloat())

                soundOptionsMenu.animate().setListener(null)
                    .translationY(0f)
            }

            override fun onTap() {
                soundOptionsMenu.setVisibility(View.VISIBLE)
                //                openSoundOptions.setVisibility(View.GONE);
                openSoundOptions.animate()
                    .translationY(openSoundOptions.getHeight().toFloat())

                soundOptionsMenu.animate().setListener(null)
                    .translationY(0f)
            }

        })

        playButton.setOnClickListener(View.OnClickListener {
            if (mediaPlayer != null) {

                isPlayClicked = true

                if (mediaPlayer!!.isPlaying()) {
                    mediaPlayer!!.pause()
                    playButton.setImageResource(R.drawable.play)
                } else {
                    mediaPlayer!!.start()
                    playButton.setImageResource(R.drawable.pause)
                }
            }
        })

        previousButton.setOnClickListener(View.OnClickListener {
            if (mediaPlayer != null) {

                if (isPlayClicked) {
                    if (quranPreferences.getBoolean(Constants.TRANSLATION_STATE, false)) {

                        if (ayaNumber > 1) {
                            clickedCounter = 0
                            scrollPosition--
                            ayaNumber--
                            allAudioPaths?.clear()

                            val audioFolderPath =
                                baseDir.absolutePath + "/BeBetterMuslim/Audio/" +  reciter  /*"AbdulRahman"*/ + "/" + surahNumber + "/"

                            for (i in ayaNumber - 1 until totalAya) {
                                val path = audioFolderPath + HelperFunctions.formatNumber(surahNumber) +
                                        HelperFunctions.formatNumber(i + 1) + ".mp3"
                                allAudioPaths?.add(path)
                                println(path)
                            }
                            println("----------------------------------")

                            linearLayoutManager?.scrollToPositionWithOffset(scrollPosition, 0)
                            try {
                                fis = FileInputStream(allAudioPaths?.get(clickedCounter))
                                fd = fis.fd
                                fd?.let {
                                    if (it.valid()) {

                                        mediaPlayer!!.stop()
                                        mediaPlayer!!.reset()
                                        mediaPlayer!!.setDataSource(fd)
                                        mediaPlayer!!.prepare()
                                        mediaPlayer!!.start()
                                        playButton.setImageResource(R.drawable.pause)
                                    }
                                }

                            } catch (e: IOException) {
                                e.printStackTrace()
                            }

                        }


                    } else {
                        val clickableSpans = allSpans.get(surahNumber - 1)

                        if (ayaNumber > 1) {

                            clickableSpans.get(ayaNumber - 1).setHighlightWord(false)
                            ayaNumber--
                            clickedCounter = 0

                            val clickableSpan = clickableSpans.get(ayaNumber - 1)
                            clickableSpan.setHighlightWord(true)

                            System.out.println(clickableSpan.getAyaText())

                            val audioFolderPath =
                                baseDir.absolutePath + "/BeBetterMuslim/Audio/" +  reciter  /*"AbdulRahman"*/ + "/" + surahNumber + "/"

                            try {

                                allAudioPaths?.clear()

                                for (i in ayaNumber - 1 until clickableSpans.size) {
                                    val path = audioFolderPath + HelperFunctions.formatNumber(surahNumber) +
                                            HelperFunctions.formatNumber(i + 1) + ".mp3"

                                    allAudioPaths?.add(path)
                                }

                                fis = FileInputStream(allAudioPaths?.get(0))
                                fd = fis.fd
                                fd?.let {
                                    if (it.valid()) {
                                        mediaPlayer!!.stop()
                                        mediaPlayer!!.reset()
                                        mediaPlayer!!.setDataSource(fd)
                                        mediaPlayer!!.prepare()
                                        mediaPlayer!!.start()
                                        playButton.setImageResource(R.drawable.pause)
                                    }
                                }
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }

                            recyclerView.adapter?.notifyDataSetChanged()
                        }
                    }
                }
            }
        })

        nextButton.setOnClickListener(View.OnClickListener {
            if (mediaPlayer != null) {
                if (isPlayClicked) {

                    if (quranPreferences.getBoolean(Constants.TRANSLATION_STATE, false)) {

                        if (clickedCounter < allAudioPaths!!.size - 1) {
                            clickedCounter++
                            scrollPosition++
                            ayaNumber++
                            linearLayoutManager?.scrollToPositionWithOffset(scrollPosition, 0)
                            try {
                                fis = FileInputStream(allAudioPaths!!.get(clickedCounter))
                                fd = fis.fd

                                fd?.let {
                                    if (it.valid()) {

                                        mediaPlayer!!.stop()
                                        mediaPlayer!!.reset()
                                        mediaPlayer!!.setDataSource(fd)
                                        mediaPlayer!!.prepare()
                                        mediaPlayer!!.start()
                                        playButton.setImageResource(R.drawable.pause)
                                    }
                                }
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }

                        }
                    } else {

                        val clickableSpans = allSpans.get(surahNumber - 1)

                        if (ayaNumber < clickableSpans.size) {

                            clickableSpans.get(ayaNumber - 1).setHighlightWord(false)
                            ayaNumber++
                            clickedCounter++

                            val clickableSpan = clickableSpans.get(ayaNumber - 1)
                            clickableSpan.setHighlightWord(true)

                            System.out.println(clickableSpan.getAyaText())
                            System.out.println(clickableSpan.getAya())


                            try {

                                val audioFolderPath =
                                    baseDir.absolutePath + "/BeBetterMuslim/Audio/" +  reciter  /*"AbdulRahman" */+ "/" + surahNumber + "/"
                                val path = audioFolderPath + HelperFunctions.formatNumber(surahNumber) +
                                        HelperFunctions.formatNumber(ayaNumber) + ".mp3"

                                fis = FileInputStream(path)
                                fd = fis.fd

                                fd?.let {
                                    if (it.valid()) {

                                        mediaPlayer!!.stop()
                                        mediaPlayer!!.reset()
                                        mediaPlayer!!.setDataSource(fd)
                                        mediaPlayer!!.prepare()
                                        mediaPlayer!!.start()
                                        playButton.setImageResource(R.drawable.pause)
                                    }
                                }
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }

                            recyclerView.adapter?.notifyDataSetChanged()
                        }
                    }
                }
            }
        })

        // Volume Controls Start

        volumeControlStream = AudioManager.STREAM_MUSIC

        volumeSeekBar.setMax(1000)

        try {
            audioManager = getSystemService(Context.AUDIO_SERVICE) as AudioManager
            volumeSeekBar.max = audioManager!!.getStreamMaxVolume(AudioManager.STREAM_MUSIC)
            volumeSeekBar.progress = audioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC)

            volumeSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                override fun onStopTrackingTouch(arg0: SeekBar) {}

                override fun onStartTrackingTouch(arg0: SeekBar) {}

                override fun onProgressChanged(arg0: SeekBar, progress: Int, arg2: Boolean) {
                    audioManager?.setStreamVolume(
                        AudioManager.STREAM_MUSIC,
                        progress, 0
                    )
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Volume Controls End

    }

    override fun onStop() {
        super.onStop()
        if (mediaPlayer != null) {
            if (mediaPlayer?.isPlaying!!) {
                mediaPlayer?.stop()
                mediaPlayer?.reset()
                isPlayClicked = false
            }
        }

    }

    fun scrollTo(rowPosition: Int) {
        linearLayoutManager?.scrollToPositionWithOffset(rowPosition, 0)
    }

    fun updateScreenState() {
        val screenState = quranPreferences.getBoolean(Constants.SCREEN_STATE, true)

        if (screenState) {
            window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        } else {
            window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    fun updateListView(isTranslation: Boolean, translationFileName: String?) {
        FileDownloader.init(this)
        val translationPath = Environment.getExternalStorageDirectory().toString() + "/BeBetterMuslim/Translations/"
        sections = ArrayList()

        val helperFunctions = HelperFunctions()

        if (isTranslation) {

            val arabicXML = helperFunctions.readXMLFromAssets(this@QuranActivity, "Arabic.xml")
            val translationXML: String

            if (translationFileName == "English.xml" || translationFileName == "Urdu.xml") {
                translationXML = helperFunctions.readXMLFromAssets(this@QuranActivity, translationFileName)
            } else {
                translationXML = helperFunctions.readXMLFromFile(translationPath + translationFileName!!)
            }

            try {

                quranArabic = gsonXml.fromXml(arabicXML, Quran::class.java)
                val quranTranslation = gsonXml.fromXml(translationXML, Quran::class.java)

                surahArabic = quranArabic.sura
                val surahTranslation = quranTranslation.sura

                var sectionIndex = 0

                val withTranslationHolders = ArrayList<WithTranslationHolder>()

                for (i in surahArabic.indices) {

                    val aya = surahArabic[i].aya
                    val transAya = surahTranslation[i].getAya()

                    if (i == 0 || i == 8) {
                        sections.add(SimpleSectionedRecyclerViewAdapter.Section(sectionIndex, surahArabic[i].name, -1))
                    } else {
                        sections.add(
                            SimpleSectionedRecyclerViewAdapter.Section(
                                sectionIndex,
                                surahArabic[i].name,
                                R.drawable.bismillah
                            )
                        )
                    }

                    for (j in aya.indices) {
                        withTranslationHolders.add(
                            WithTranslationHolder(
                                Integer.parseInt(surahArabic[i].index),
                                Integer.parseInt(aya[j].index),
                                aya[j].text,
                                transAya[j].getText(),
                                aya.size
                            )
                        )

                        sectionIndex++
                    }
                }

                val withTranslationAdapter =
                    WithTranslationAdapter(withTranslationHolders,
                        CustomOnClickListener { v, rowPosition, holder ->
                            val sourceURLS = ArrayList<String>()
                            val destinationURLS = ArrayList<String>()

                            scrollPosition = rowPosition + holder.surahNumber

                            val audioFolderPath =
                                baseDir.absolutePath + "/BeBetterMuslim/Audio/" +  reciter  /*"AbdulRahman"*/ + "/" + holder.surahNumber + "/"
                            val audioFolder = File(audioFolderPath)

                            val stringSurahNumber = HelperFunctions.formatNumber(holder.surahNumber)
                            var stringAyaNumber: String

                            for (i in holder.ayaNumber..holder.totalAya) {
                                stringAyaNumber = HelperFunctions.formatNumber(i)
                                val fileName = "$stringSurahNumber$stringAyaNumber.mp3"
                                sourceURLS.add(QURAN_AUDIO_PATH +  reciter  /*"AbdulRahman"*/ + "/" + holder.surahNumber + "/" + fileName)
                                destinationURLS.add("$audioFolderPath$stringSurahNumber$stringAyaNumber.mp3")
                            }

                            if (!audioFolder.exists()) {
                                audioFolder.mkdirs()
                            }
                            if (storagePermissionFlag) {
                            val audioFiles = audioFolder.listFiles()
                            Log.d("Files Length", "" + audioFiles.size)

                            totalAya = holder.totalAya
                            ayaNumber = holder.ayaNumber
                            surahNumber = holder.surahNumber

                            if (FileDownloader.getImpl().isServiceConnected) {
                                FileDownloader.getImpl().pauseAll()
                            }

                            if (audioFiles.size == holder.totalAya) {

                                allAudioPaths = ArrayList()
                                for (i in destinationURLS.indices) {
                                    allAudioPaths?.add(destinationURLS[i])
                                }

                                try {
                                    fis = FileInputStream(allAudioPaths?.get(0))
                                    fd = fis.fd
                                    fd?.let {

                                        if (it.valid()) {

                                            mediaPlayer!!.reset()
                                            mediaPlayer!!.setDataSource(fd)
                                            mediaPlayer!!.prepare()
                                            mediaPlayer!!.start()
                                            playButton.setImageResource(R.drawable.pause)
                                            playButton.isEnabled = true
                                            nextButton.isEnabled = true
                                            previousButton.isEnabled = true
                                            isPlayClicked = true
                                        }
                                    }

                                    println("Audio File Length: " + audioFiles.size + ", Total Aya: " + holder.totalAya)

                                } catch (e: IOException) {
                                    e.printStackTrace()
                                }

                            } else {
                                allAudioPaths = ArrayList()

                                val queueTarget = object : FileDownloadListener() {
                                    override fun pending(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {}

                                    override fun connected(
                                        task: BaseDownloadTask,
                                        etag: String?,
                                        isContinue: Boolean,
                                        soFarBytes: Int,
                                        totalBytes: Int
                                    ) {
                                        Log.d("Connected", "" + task.downloadId)
                                    }

                                    override fun progress(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {}

                                    override fun blockComplete(task: BaseDownloadTask?) {}

                                    override fun retry(
                                        task: BaseDownloadTask,
                                        ex: Throwable?,
                                        retryingTimes: Int,
                                        soFarBytes: Int
                                    ) {
                                        task.start()
                                    }

                                    override fun completed(task: BaseDownloadTask) {
                                        //                                Log.d("Completed", "Completed");

                                        allAudioPaths?.add(task.path)

                                        if (allAudioPaths?.size == 1) {

                                            try {
                                                playButton.setImageResource(R.drawable.pause)
                                                playButton.isEnabled = true
                                                nextButton.isEnabled = true
                                                previousButton.isEnabled = true
                                                fis = FileInputStream(allAudioPaths?.get(0))
                                                fd = fis.fd
                                                fd?.let {

                                                    if (it.valid()) {

                                                        mediaPlayer!!.reset()
                                                        mediaPlayer!!.setDataSource(fd)
                                                        mediaPlayer!!.prepare()
                                                        mediaPlayer!!.start()
                                                        playButton.setImageResource(R.drawable.pause)
                                                        playButton.isEnabled = true
                                                        nextButton.isEnabled = true
                                                        previousButton.isEnabled = true
                                                        isPlayClicked = true
                                                    }
                                                }

                                            } catch (e: IOException) {
                                                e.printStackTrace()
                                            }

                                        }
                                    }

                                    override fun paused(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {}

                                    override fun error(task: BaseDownloadTask, e: Throwable) {
                                        e.printStackTrace()
                                    }

                                    override fun warn(task: BaseDownloadTask) {}
                                }

                                for (i in sourceURLS.indices) {
                                    FileDownloader.getImpl().create(sourceURLS[i]).setPath(destinationURLS[i])
                                        .setAutoRetryTimes(5)
                                        .setCallbackProgressTimes(0)
                                        .setListener(queueTarget)
                                        .ready()
                                }

                                if (HelperFunctions.isOnline(this@QuranActivity)) {
                                    FileDownloader.getImpl().start(queueTarget, true)
                                } else {
                                    Toast.makeText(this@QuranActivity, "No internet connection!", Toast.LENGTH_SHORT)
                                        .show()
                                }
                            }
                        }
                            clickedCounter = 0

                            mediaPlayer?.setOnCompletionListener(MediaPlayer.OnCompletionListener { mp ->
                                mediaPlayer?.stop()
                                mediaPlayer?.reset()

                                clickedCounter++
                                scrollPosition++

                                if (clickedCounter < allAudioPaths!!.size) {
                                    try {
                                        fis = FileInputStream(allAudioPaths?.get(clickedCounter))

                                        fd = fis.fd

                                        if (fd != null) {
                                            mediaPlayer?.setDataSource(fd)
                                            mediaPlayer?.prepare()
                                            mediaPlayer?.start()
                                            ayaNumber++
                                            println("Scroll Positon: $scrollPosition")

                                            if (scrollState) {
                                                linearLayoutManager?.scrollToPositionWithOffset(scrollPosition, 0)
                                            }

                                        }

                                    } catch (e: IOException) {
                                        e.printStackTrace()
                                    }

                                } else {
                                    mp.stop()
                                    mp.reset()
                                    playButton.setImageResource(R.drawable.play)
                                }
                            })
                        })

                val dummy = arrayOfNulls<SimpleSectionedRecyclerViewAdapter.Section>(sections.size)
                val mSectionedAdapter = SimpleSectionedRecyclerViewAdapter(
                    this,
                    R.layout.section,
                    R.id.section_text,
                    withTranslationAdapter
                )
                mSectionedAdapter.setSections(sections.toTypedArray())
                mSectionedAdapter.setIsTranslation(true)

                recyclerView.adapter = mSectionedAdapter

            } catch (e: Throwable) {
                e.printStackTrace()
                editor.putBoolean(Constants.TRANSLATION_STATE, false)
                editor.apply()
                Toast.makeText(this@QuranActivity, "Error in Internet Connection", Toast.LENGTH_SHORT).show()
                val file = File(translationPath + translationFileName)
                file.delete()
                updateListView(false, null)
            }


        } else {
            val arabicXML = helperFunctions.readXMLFromAssets(this@QuranActivity, "Arabic.xml")
            quranArabic = gsonXml.fromXml(arabicXML, Quran::class.java)
            surahArabic = quranArabic.sura

            //            Aya[] firstAya = surahArabic[0].getAya();
            //            String arabicBismillah = firstAya[0].getText();

            val withoutTranslationHolders = ArrayList<WithoutTranslationHolder>()

            var sectionIndex = 0

            allSpans = ArrayList()

            var positionCounter = 0

            for (i in surahArabic.indices) {
                val aya = surahArabic[i].aya

                if (i == 0 || i == 8) {
                    sections.add(SimpleSectionedRecyclerViewAdapter.Section(sectionIndex, surahArabic[i].name, -1))
                } else {
                    sections.add(
                        SimpleSectionedRecyclerViewAdapter.Section(
                            sectionIndex,
                            surahArabic[i].name,
                            R.drawable.bismillah
                        )
                    )
                }

                var sb = SpannableStringBuilder()
                val clickableSpanArrayList = ArrayList<MyClickableSpan>()

                for (j in aya.indices) {

                    clickableSpanArrayList.add(object : MyClickableSpan(
                        aya[j].text,
                        Integer.parseInt(surahArabic[i].index),
                        Integer.parseInt(aya[j].index),
                        positionCounter
                    ) {
                        override fun onClick(widget: View) {

                            for (i in clickableSpanArrayList.indices) {
                                clickableSpanArrayList[i].isHighlightWord = false
                            }

                            isHighlightWord = true

                            widget.invalidate()

                            surahNumber = surah
                            ayaNumber = getAya()
                            val totalAya = clickableSpanArrayList.size

                            val sourceURLS = ArrayList<String>()
                            val destinationURLS = ArrayList<String>()

                            val stringSurahNumber = HelperFunctions.formatNumber(surahNumber)
                            var stringAyaNumber: String

                            val audioFolderPath =
                                baseDir.absolutePath + "/BeBetterMuslim/Audio/" +  reciter  /*"AbdulRahman"*/ + "/" + surahNumber + "/"

                            val audioFolder = File(audioFolderPath)

                            var isFolderCreated = false
                            if (!audioFolder.exists()) {
//                                isFolderCreated = audioFolder.mkdirs()
                                isFolderCreated = audioFolder.mkdirs()
                            }
                            if (storagePermissionFlag) {
                                for (i in ayaNumber..totalAya) {

                                    stringAyaNumber = HelperFunctions.formatNumber(i)

                                    val fileName = "$stringSurahNumber$stringAyaNumber.mp3"
                                    sourceURLS.add(QURAN_AUDIO_PATH +  reciter  /*"AbdulRahman"*/ + "/" + surahNumber + "/" + fileName)
                                    destinationURLS.add("$audioFolderPath$stringSurahNumber$stringAyaNumber.mp3")
                                }

                                allAudioPaths = ArrayList()

                                val audioFiles: Array<File> = audioFolder.listFiles()

                                if (FileDownloader.getImpl().isServiceConnected) {
                                    FileDownloader.getImpl().pauseAll()
                                }
                                //todo audio files null here
                                if (audioFiles.size == totalAya) {
//todo continue from here
                                    allAudioPaths?.addAll(destinationURLS)

                                    try {
                                        fis = FileInputStream(allAudioPaths?.get(0))
                                        fd = fis.fd
                                        fd?.let {
                                            if (it.valid()) {
                                                mediaPlayer?.reset()
                                                mediaPlayer?.setDataSource(fd)
                                                mediaPlayer?.prepare()
                                                mediaPlayer?.start()
                                                playButton.setImageResource(R.drawable.pause)
                                                playButton.isEnabled = true
                                                nextButton.isEnabled = true
                                                previousButton.isEnabled = true
                                                isPlayClicked = true

                                            }
                                        }


                                    } catch (e: IOException) {
                                        e.printStackTrace()
                                    }

                                } else {

                                    val queueTarget = object : FileDownloadListener() {
                                        override fun pending(
                                            task: BaseDownloadTask,
                                            soFarBytes: Int,
                                            totalBytes: Int
                                        ) {
                                        }

                                        override fun connected(
                                            task: BaseDownloadTask,
                                            etag: String?,
                                            isContinue: Boolean,
                                            soFarBytes: Int,
                                            totalBytes: Int
                                        ) {
                                            Log.d("Connected", "" + task.downloadId)
                                        }

                                        override fun progress(
                                            task: BaseDownloadTask,
                                            soFarBytes: Int,
                                            totalBytes: Int
                                        ) {
                                        }

                                        override fun blockComplete(task: BaseDownloadTask?) {}

                                        override fun retry(
                                            task: BaseDownloadTask?,
                                            ex: Throwable?,
                                            retryingTimes: Int,
                                            soFarBytes: Int
                                        ) {
                                            //                                        task.start();
                                        }

                                        override fun completed(task: BaseDownloadTask) {
                                            //                                Log.d("Completed", "Completed");

                                            allAudioPaths?.add(task.path)

                                            if (allAudioPaths?.size == 1) {

                                                try {
                                                    playButton.setImageResource(R.drawable.pause)
                                                    playButton.isEnabled = true
                                                    nextButton.isEnabled = true
                                                    previousButton.isEnabled = true
                                                    fis = FileInputStream(allAudioPaths?.get(0))
                                                    fd = fis.fd
                                                    fd?.let {
                                                        if (it.valid()) {
                                                            mediaPlayer?.reset()
                                                            mediaPlayer?.setDataSource(fd)
                                                            mediaPlayer?.prepare()
                                                            mediaPlayer?.start()
                                                        }

                                                    }
                                                } catch (e: IOException) {
                                                    e.printStackTrace()
                                                }

                                            }
                                        }

                                        override fun paused(task: BaseDownloadTask, soFarBytes: Int, totalBytes: Int) {}

                                        override fun error(task: BaseDownloadTask, e: Throwable) {
                                            e.printStackTrace()
                                        }

                                        override fun warn(task: BaseDownloadTask) {}
                                    }

                                    for (i in sourceURLS.indices) {
                                        FileDownloader.getImpl().create(sourceURLS[i]).setPath(destinationURLS[i])
                                            .setAutoRetryTimes(5)
                                            .setCallbackProgressTimes(0) // why do this? in here i assume do not need callback each task's `FileDownloadListener#progress`, so in this way reduce ipc will be effective optimization
                                            .setListener(queueTarget)
                                            .ready()
                                    }

                                    if (HelperFunctions.isOnline(this@QuranActivity)) {
                                        FileDownloader.getImpl().start(queueTarget, true)
                                    } else {
                                        Toast.makeText(
                                            this@QuranActivity,
                                            "No internet connection!",
                                            Toast.LENGTH_SHORT
                                        )
                                            .show()
                                    }
                                }

                            }

                            clickedCounter = 0

                            mediaPlayer?.setOnCompletionListener(MediaPlayer.OnCompletionListener { mp ->
                                mediaPlayer?.stop()
                                mediaPlayer?.reset()

                                clickedCounter++

                                if (clickedCounter < allAudioPaths!!.size) {
                                    try {
                                        fis = FileInputStream(allAudioPaths?.get(clickedCounter))

                                        fd = fis.fd

                                        if (fd != null) {
                                            mediaPlayer?.setDataSource(fd)
                                            mediaPlayer?.prepare()
                                            mediaPlayer?.start()
                                            clickableSpanArrayList[ayaNumber - 1].isHighlightWord = false
                                            val currentSpan = clickableSpanArrayList[ayaNumber++]
                                            currentSpan.isHighlightWord = true

                                            val parentTextView = widget as TextView
                                            val completeText = parentTextView.text as SpannableString
                                            val textViewLayout = parentTextView.layout

                                            val tempString = currentSpan.ayaText

                                            recyclerView.adapter?.notifyDataSetChanged()

                                            val parentTextViewRect = Rect()

                                            val startOffsetOfClickedText =
                                                completeText.getSpanStart(tempString).toDouble()
                                            //                                                double endOffsetOfClickedText = completeText.getSpanEnd(tempString);
                                            //                                                double startXCoordinatesOfClickedText = textViewLayout.getPrimaryHorizontal((int) startOffsetOfClickedText);
                                            //                                                double endXCoordinatesOfClickedText = textViewLayout.getPrimaryHorizontal((int) endOffsetOfClickedText);


                                            // Get the rectangle of the clicked text
                                            val currentLineStartOffset =
                                                textViewLayout.getLineForOffset(startOffsetOfClickedText.toInt())
                                            //                                                int currentLineEndOffset = textViewLayout.getLineForOffset((int) endOffsetOfClickedText);
                                            //                                                boolean keywordIsInMultiLine = currentLineStartOffset != currentLineEndOffset;
                                            //                                                System.out.println("Location");
                                            //                                                System.out.println("Y: " + textViewLayout.getLineBounds(currentLineStartOffset, parentTextViewRect));

                                            if (scrollState) {
                                                linearLayoutManager?.scrollToPositionWithOffset(
                                                    currentSpan.position + 1,
                                                    textViewLayout.getLineBounds(
                                                        currentLineStartOffset,
                                                        parentTextViewRect
                                                    )
                                                )
                                            }

                                        }

                                    } catch (e: IOException) {
                                        e.printStackTrace()

//                                        if(e.message!!.contains("No File")){
//                                            FileDownloader.getImpl().create(sourceURLS[sourceURLS.size-1]).setPath(destinationURLS[destinationURLS.size -1])
//                                                .setAutoRetryTimes(5)
//                                                .setCallbackProgressTimes(0) // why do this? in here i assume do not need callback each task's `FileDownloadListener#progress`, so in this way reduce ipc will be effective optimization
//                                                .setListener(queueTarget)
//                                                .ready()
//                                            if (HelperFunctions.isOnline(this@QuranActivity)) {
//                                                FileDownloader.getImpl().start(queueTarget, true)
//                                            } else {
//                                                Toast.makeText(
//                                                    this@QuranActivity,
//                                                    "No internet connection!",
//                                                    Toast.LENGTH_SHORT
//                                                )
//                                                    .show()
//                                            }
//                                        }
                                    }

                                } else {
                                    mp.stop()
                                    mp.reset()
                                    playButton.setImageResource(R.drawable.play)
                                }
                            })

                        }
                    })


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        sb.append(aya[j].text, clickableSpanArrayList[j], Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
                        sb.append(
                            " " + ayaSeparator + HelperFunctions.getCodeFromNumber(aya[j].index) + " ",
                            CustomTypefaceSpan("", ayaSeparatorFont),
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE
                        )
                    }

                    if ((j + 1) % 10 == 0) {
                        withoutTranslationHolders.add(WithoutTranslationHolder(SpannableString(sb)))
                        positionCounter++
                        sb = SpannableStringBuilder()
                        sectionIndex++
                    }

                }

                withoutTranslationHolders.add(WithoutTranslationHolder(SpannableString(sb)))
                positionCounter += 2
                sectionIndex++

                allSpans.add(clickableSpanArrayList)
            }

            val withoutTranslationAdapter = WithoutTranslationAdapter(withoutTranslationHolders)

            val dummy = arrayOfNulls<SimpleSectionedRecyclerViewAdapter.Section>(sections.size)
            val mSectionedAdapter =
                SimpleSectionedRecyclerViewAdapter(this, R.layout.section, R.id.section_text, withoutTranslationAdapter)
            mSectionedAdapter.setSections(sections.toTypedArray())
            mSectionedAdapter.setIsTranslation(false)

            recyclerView.adapter = mSectionedAdapter

        }
    }
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(ViewPumpContextWrapper.wrap(newBase))
    }

    class MyDialogFragment : android.app.DialogFragment() {

        lateinit var surahRecyclerView: RecyclerView
        lateinit var surahDataHolders: MutableList<SurahDataHolder>

        override fun onCreateView(
            inflater: LayoutInflater?, container: ViewGroup?,
            savedInstanceState: Bundle?
        ): View? {
            val v = inflater?.inflate(R.layout.search_aya_dialog, container, false)
            surahDataHolders = ArrayList<SurahDataHolder>()

            surahRecyclerView = v!!.findViewById(R.id.surah_names_recyclerview)

            for (i in 0 until Constants.surahNames.size) {
                surahDataHolders.add(
                    SurahDataHolder(
                        sections[i].firstPosition + i,
                        i + 1,
                        Constants.surahNames[i],
                        sura[i].name
                    )
                )
            }

            val surahNameAdapter = SurahNameAdapter(surahDataHolders, object : SurahNameAdapter.SurahOnClickListener {

                override fun onItemClick(v: View, surahDataHolder: SurahDataHolder) {
                    val quranActivity = activity as QuranActivity
                    dismiss()
                    quranActivity.scrollTo(surahDataHolder.getRowPosition())
                }
            })

            val itemDecoration = DividerItemDecoration(activity, DividerItemDecoration.VERTICAL_LIST)
            surahRecyclerView.addItemDecoration(itemDecoration)
            surahRecyclerView.layoutManager = LinearLayoutManager(activity)
            surahRecyclerView.adapter = surahNameAdapter

            return v
        }

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
        }

        override fun onStart() {
            super.onStart()
            val dialog = dialog
            if (dialog != null) {
                val width = ViewGroup.LayoutParams.MATCH_PARENT
                val height = ViewGroup.LayoutParams.MATCH_PARENT
                dialog.window!!.setLayout(width, height)
            }
        }

        companion object {
            lateinit var sections: List<SimpleSectionedRecyclerViewAdapter.Section>
            lateinit var sura: Array<Sura>

            internal fun newInstance(
                sections: List<SimpleSectionedRecyclerViewAdapter.Section>,
                sura: Array<Sura>
            ): MyDialogFragment {
                val f = MyDialogFragment()
                MyDialogFragment.sections = sections
                MyDialogFragment.sura = sura
                return f
            }
        }

    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN) {
            audioManager?.let {
                volumeSeekBar.progress = audioManager!!.getStreamVolume(AudioManager.STREAM_MUSIC)
            }
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_UP) {
            audioManager?.let {
                volumeSeekBar.progress = it.getStreamVolume(AudioManager.STREAM_MUSIC)
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    override fun onResume() {
        super.onResume()
        if (quranPreferences.getBoolean(Constants.TRANSLATION_STATE, false)) {
            val translationXML = quranPreferences.getString(Constants.TRANSLATION_LANGUAGE, null)
            val file =
                File(Environment.getExternalStorageDirectory().toString() + "/BeBetterMuslim/Translations/" + translationXML + ".xml")
            if (file.exists() || translationXML!! + ".xml" == "English.xml" || "$translationXML.xml" == "Urdu.xml") {
                updateListView(true, translationXML!! + ".xml")
            } else {
                DownloadTranslationXML().execute(translationXML)
            }

        } else {
            updateListView(false, null)
        }
        mTracker?.setScreenName("Quran Activity")
        mTracker?.send(HitBuilders.ScreenViewBuilder().build())

    }

    internal fun showDialog() {

        val newFragment = MyDialogFragment.newInstance(sections, surahArabic)

        newFragment.show(fragmentManager, "dialog")
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {

        menuInflater.inflate(R.menu.menu_quran, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onDestroy() {
        super.onDestroy()
        quranPreferences.unregisterOnSharedPreferenceChangeListener(listener)
    }

    override fun onOptionsItemSelected(menuItem: MenuItem): Boolean {
        when (menuItem.itemId) {
            android.R.id.home -> {
                val homeIntent = Intent(this, MainActivity::class.java)
                homeIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(homeIntent)
            }

            R.id.quran_setting -> startActivity(Intent(this@QuranActivity, QuranSettings::class.java))

            R.id.quran_search -> showDialog()

            R.id.drawerMenuItem -> {
            }
        }
        return super.onOptionsItemSelected(menuItem)
    }

    internal inner class DownloadTranslationXML : AsyncTask<String, String, String>() {

        lateinit var inputStream: InputStream
        lateinit var url: URL
        lateinit var urlConnection: HttpURLConnection

        lateinit var mProgressDialog: ProgressDialog
        lateinit var translationFile: File
        lateinit var fileOutput: FileOutputStream

        override fun doInBackground(vararg params: String): String? {

            try {
                url = URL(QURAN_TRANSLATION_PATH + params[0] + "_Trans.xml")

                urlConnection = url.openConnection() as HttpURLConnection
                urlConnection.requestMethod = "GET"
                urlConnection.readTimeout = 10000
                urlConnection.connectTimeout = 15000
                urlConnection.doOutput = true
                urlConnection.connect()

                val translationFolder =
                    File(Environment.getExternalStorageDirectory().toString() + "/BeBetterMuslim/Translations/")
                var success = true

                if (!translationFolder.exists()) {
                    success = translationFolder.mkdirs()
                    Log.d("Directory: ", "" + success)
                }

                if (success) {
                    translationFile = File(translationFolder, params[0] + ".xml")
                    fileOutput = FileOutputStream(translationFile)
                    inputStream = urlConnection.inputStream

                    val totalSize = urlConnection.contentLength
                    var downloadedSize = 0
                    val buffer = ByteArray(1024)
                    var bufferLength = 0 //used to store a temporary size of the buffer

                    while (inputStream.read(buffer) > 0) {
                        bufferLength = inputStream.read(buffer)
                        fileOutput.write(buffer, 0, bufferLength)
                        downloadedSize += bufferLength
                        val progress = downloadedSize * 100 / totalSize
                        publishProgress("" + progress)
                    }

                    fileOutput.close()
                }

                return params[0]

            } catch (e: IOException) {
                e.printStackTrace()
            }

            return null
        }

        override fun onPreExecute() {
            super.onPreExecute()
            showProgressDialog()
        }

        override fun onProgressUpdate(vararg progress: String) {
            mProgressDialog.progress = Integer.parseInt(progress[0])
        }

        override fun onPostExecute(result: String?) {
            mProgressDialog.dismiss()
            Log.v("Download State: ", "Complete")

            if (result == null) {
                Toast.makeText(this@QuranActivity, "Error in downloading Translation", Toast.LENGTH_SHORT).show()
                updateListView(false, null)
                editor.putBoolean(Constants.TRANSLATION_STATE, false)
                editor.commit()
            } else {
                updateListView(true, "$result.xml")
            }
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        fun showProgressDialog() {
            mProgressDialog = ProgressDialog(this@QuranActivity)
            mProgressDialog.isIndeterminate = false
            mProgressDialog.setMessage("Downloading Translation..")
            mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL)
            mProgressDialog.setCancelable(false)
            mProgressDialog.max = 100
            mProgressDialog.create()
            mProgressDialog.show()
        }
    }


}
