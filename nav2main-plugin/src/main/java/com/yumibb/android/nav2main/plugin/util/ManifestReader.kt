package com.yumibb.android.nav2main.plugin.util

import org.xml.sax.Attributes
import org.xml.sax.helpers.DefaultHandler
import java.io.File
import javax.xml.parsers.SAXParserFactory

/**
 * Merged AndroidManifest Reader
 *
 * @author y.huang
 * @since 2019-10-21
 */
class ManifestReader {

    companion object {


        /**
         * reader merged AndroidManifest file,return all acitivities
         */
        internal fun activities(manifest: File, exclude: Set<String>?, packagePres: MutableSet<String>?): Set<String> {


            var activities = mutableSetOf<String>()

            if (!manifest.exists()) {
                return activities
            }

            val sAXParserFactory = SAXParserFactory.newInstance()
            val sAXParser = sAXParserFactory.newSAXParser()


            var handler = object : DefaultHandler() {

                override fun startElement(uri: String?, localName: String?, qName: String?, attributes: Attributes?) {
                    super.startElement(uri, localName, qName, attributes)
                    if (qName.equals("activity")) {

                        attributes?.getValue("android:name".trim()).let { activity ->

                            activity?.let {

                                if (exclude != null) {
                                    var contain = exclude.contains(activity)
                                    if (!contain) {
                                        addActivity(activity, packagePres)
                                    } else Unit
                                } else {
                                    addActivity(activity, packagePres)
                                }
                            }
                        }
                    }
                }

                private fun addActivity(activity: String, packagePres: MutableSet<String>?) {


                    if (packagePres != null) {

                        packagePres.forEach {
                            if (activity.contains(it)) {
                                activities.add(activity)
                                return
                            }
                        }
                    } else {
                        activities.add(activity)
                    }

                }
            }
            sAXParser.parse(manifest, handler)
            return activities
        }
    }
}