package org.gdocument.gtracergps.launcher.util;

import java.text.DecimalFormat;

import org.gdocument.gtracergps.launcher.log.Logger;

import android.location.Location;

public class GpsUtil {

	private static final boolean log = false;
	private static final String TAG = GpsUtil.class.getCanonicalName();;
	
	private static DecimalFormat decimalFormat = (DecimalFormat) DecimalFormat.getInstance();

	static {
		decimalFormat.applyPattern("###0.##########");
	}

	public static double calDistanceKM2(double lat1, double lon1, double lat2, double lon2, double alt1, double alt2) {
		logMe("calDistanceKM2 alt");
		double distance = calDistanceKM2(lat1, lon1, lat2, lon2);

		distance = distanceWithAltitude(distance, alt1, alt2);
		
		return distance;
	}

	public static double calDistanceKM(double lat1, double lon1, double lat2, double lon2, double alt1, double alt2) {
		logMe("calDistanceKM alt");
		double distance = calDistanceKM(lat1, lon1, lat2, lon2);

		distance = distanceWithAltitude(distance, alt1, alt2);
		
		return distance;
	}

	public static double calDistanceKM2(double lat1, double lon1, double lat2, double lon2) {
		logMe("calDistanceKM2");
		float[] results = new float[1];
		Location.distanceBetween(lat1, lon1, lat2, lon2, results);
		
		return results[0];
	}

	public static double calDistanceKM3(double lat1, double lon1, double lat2, double lon2, double alt1, double alt2) {
		logMe("calDistanceKM alt");

		//http://bluemm.blogspot.fr/2007/01/excel-formula-to-calculate-distance.html
		double distance = Math.acos(Math.cos(Math.toRadians(90-lat1)) * Math.cos(Math.toRadians(90-lat2)) + Math.sin(Math.toRadians(90-lat1)) * Math.sin(Math.toRadians(90-lat2)) * Math.cos(Math.toRadians(lon1-lon2))) * 6371.0d;

		distance = distanceWithAltitude(distance, alt1, alt2);
		
		return distance;
	}

	public static double calDistanceKM(double lat1, double lon1, double lat2, double lon2) {
		logMe("calDistanceKM");

		// r
		int r = 6366;

		long tempsT1;
		long tempsT2;

		if (log)
			logMe("Point A (lat/lon) : " + decimalFormat.format(lat1) + " " + decimalFormat.format(lon1) + "n" + "Point B (lat/lon) : "
					+ decimalFormat.format(lat2) + " " + decimalFormat.format(lon2));

		// Conversion des entrées en ° vers en radian
		lat1 = Math.toRadians(lat1);
		lon1 = Math.toRadians(lon1);
		lat2 = Math.toRadians(lat2);
		lon2 = Math.toRadians(lon2);

		tempsT1 = System.nanoTime();
		double distance = distanceVolOiseauEntre2PointsAvecPrécision(lat1, lon1, lat2, lon2);
		tempsT2 = System.nanoTime();
		logMe("Temps (AvecPrécision) : " + String.format("%10d", (tempsT2 - tempsT1)) + " ns");
		double distanceEnKm = distance * r;

		if (log)
			logMe("Distance      : " + decimalFormat.format(distance) + " (" + distance + ")n"
					+ "Distance (km) calcul précis pour courtes distances         : " + decimalFormat.format(distanceEnKm) + " km (" + distanceEnKm + ")n"
					+ ")n" + "");
		
		return distanceEnKm;
	}

	public static double calDistanceDistantKM(double lat1, double lon1, double lat2, double lon2) {
		logMe("calDistanceDistantKM");

		// r
		int r = 6366;

		long tempsT1;
		long tempsT2;

		if (log)
			logMe("Point A (lat/lon) : " + decimalFormat.format(lat1) + " " + decimalFormat.format(lon1) + "n" + "Point B (lat/lon) : "
					+ decimalFormat.format(lat2) + " " + decimalFormat.format(lon2));

		/**
		 * Conversion des entrées en ° vers en radian
		 */
		lat1 = Math.toRadians(lat1);
		lon1 = Math.toRadians(lon1);
		lat2 = Math.toRadians(lat2);
		lon2 = Math.toRadians(lon2);

		tempsT1 = System.nanoTime();
		double distanceEloigné = distanceVolOiseauEntre2PointsSansPrécision(lat1, lon1, lat2, lon2);
		tempsT2 = System.nanoTime();
		logMe("Temps (SansPrécision) : " + String.format("%10d", (tempsT2 - tempsT1)) + " ns");
		double distanceEloignéEnKm = distanceEloigné * r;

		if (log)
			logMe("Distance Eloigné      : " + decimalFormat.format(distanceEloigné) + " (" + distanceEloigné + ")n"
					+ "Distance Eloigné (km) calcul non précis pour distances non courtes : " + decimalFormat.format(distanceEloignéEnKm) + " km (" + distanceEloignéEnKm
					+ ")n" + "");
		
		return distanceEloignéEnKm;
	}

	/**
	 * Distance entre 2 points GPS
	 * http://dotclear.placeoweb.com/post/Formule-de-calcul-entre-2-points-wgs84-pour-calculer-la-distance-qui-separe-ces-deux-points
	 * 
	 * La distance mesurée le long d'un arc de grand cercle entre deux points
	 * dont on connaît les coordonnées {lat1,lon1} et {lat2,lon2} est donnée par
	 * : d=acos(sin(lat1)*sin(lat2)+cos(lat1)*cos(lat2)*cos(lon1-lon2)) Le tout
	 * * 6366 pour l'avoir en km
	 * 
	 * @param lat1
	 * @param lon1
	 * @param lat2
	 * @param lon2
	 * @return
	 */
	private static double distanceVolOiseauEntre2PointsSansPrécision(double lat1, double lon1, double lat2, double lon2) {
		logMe("distanceVolOiseauEntre2PointsSansPrécision");

		// d=acos(sin(lat1)*sin(lat2)+cos(lat1)*cos(lat2)*cos(lon1-lon2))

		return Math.acos(
				Math.sin(lat1) * Math.sin(lat2) + Math.cos(lat1) * Math.cos(lat2) * Math.cos(lon1 - lon2)
			);

	}

	/**
	 * Distance entre 2 points GPS
	 * http://dotclear.placeoweb.com/post/Formule-de-
	 * calcul-entre-2-points-wgs84-
	 * pour-calculer-la-distance-qui-separe-ces-deux-points
	 * 
	 * La distance mesurée le long d'un arc de grand cercle entre deux points
	 * dont on connaît les coordonnées {lat1,lon1} et {lat2,lon2} est donnée par
	 * : La formule, mathématiquement équivalente, mais moins sujette aux
	 * erreurs d'arrondis pour les courtes distances est : *
	 * d=2*asin(sqrt((sin((lat1-lat2)/2))^2 + cos(lat1)*cos(lat2)*(sin((lon1-
	 * lon2)/2))^2)) Le tout * 6366 pour l'avoir en km
	 * 
	 * @param lat1
	 * @param lon1
	 * @param lat2
	 * @param lon2
	 * @return
	 */
	private static double distanceVolOiseauEntre2PointsAvecPrécision(double lat1, double lon1, double lat2, double lon2) {
		logMe("distanceVolOiseauEntre2PointsAvecPrécision");

		// d=2*asin(sqrt((sin((lat1-lat2)/2))^2 +
		// cos(lat1)*cos(lat2)*(sin((lon1- lon2)/2))^2))

		return 2 * Math.asin(
				Math.sqrt(
						Math.pow((Math.sin((lat1 - lat2) / 2)), 2) + Math.cos(lat1) * Math.cos(lat2) * (Math.pow(Math.sin(((lon1 - lon2) / 2)), 2))
					)
			);

	}

	private static void logMe(String msg) {
			Logger.logMe(TAG, msg);
    }

	/**
	 * http://www.developpez.net/forums/d1183566/php/php-sgbd/php-mysql/calcul-distance-entre-2-points-connaissant-latitude-longitude-chacun/ 
	 * @param distance
	 * @param alt1
	 * @param alt2
	 * @return
	 */
	private static double distanceWithAltitude(double distance, double alt1, double alt2) {
		logMe("distanceWithAltitude");
		//recuperation altitude en km
		alt1 = alt1/1000;
		alt2 = alt2/1000;

		distance = Math.sqrt(Math.pow(distance,2)+Math.pow(alt1-alt2,2));
		
		return distance;
	}
}
