/*
 * Copyleft 2007-2011 Ozgur Yazilim A.S.
 *
 * Distributable under LGPL license.
 * See terms of license at gnu.org.
 * http://www.gnu.org/licenses/lgpl.html
 *
 * www.tekir.com.tr
 * www.ozguryazilim.com.tr
 *
 */

package net.paramount.model;

/**
 * 
 * @author sozmen
 */
public enum TenderStatus {
	Open, // Onaysiz,
	Approved, // MusteriOnayli,
	Processing, // Isleniyor,
	Reject, // Red,
	Revisioned, // Revize edildi
	Closed // Kapandı
}
