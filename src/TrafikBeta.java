import acm.graphics.*;
import acm.program.*;
import acm.util.RandomGenerator;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.Color;

public class TrafikBeta extends GraphicsProgram {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	GRect 
	otoyol_arka_plan
	;
	GLabel puan_metni,//Puan durumu, engele carptigindaki oyun bitti ve devam et metinlerini olusturur.
	aciklama1,
	aciklama2;
	boolean puan_kontrol,//Puan arttirmak icin kontrol
	Yandimi,//Engele carptiginda oyunu durdurmak icin
	sola_gec,//Sola gecmeyi kontrol eder
	saga_gec,//Saga gecmeyi kontrol eder
	animasyon_basla=false,//Ana menudeki yazi animasyon efektleri icin
	animasyon_otoSur=false,/////
	animasyon_puanTablosu=false,////
	animasyon_cikis=false,//Ana menudeki yazi animasyon efektleri icin
	basla_dedi = false,//Menudeki basla komutunu secmeyi kontrol eder
	otomatik_oyna = false,//Menudeki oto surus komutunu secmeyi kontrol eder
	menudeyiz=false,//Menude olma halini kontrol eder
	pause_kontrol=false
	;
	GLabel basla = null, 
			oto_oyna = null,
			puan_tablosu = null,
			cikis = null;

	public static final int CAR_WIDTH = 100,//Objelerin boyut degerlerini tanimlar.
			CAR_HEIGHT = 200,
			YOL_EN = 300,
			ENGEL_EN = 100,
			ENGEL_BOY = 200;

	GImage yol1 = new GImage("images/road.jpg");
	GImage yol2 = new GImage("images/road.jpg");
	GImage yol_kenari1 = new GImage("images/Grass.jpg");
	GImage yol_kenari2 = new GImage("images/Grass.jpg");
	GImage arac = new GImage("images/car1.png");
	GImage engel = new GImage("images/car2.png");
	GImage menu_don = new GImage("images/home.png");
	GImage pause_button = new GImage("images/pause.png");

	int ekran_boy,//Acilan pencerenin boyu
	ekran_en,//Acilan pencerenin eni
	puan
	;
	double pause,
	serit_hizi,//Yoldaki seritlerin hareket hizi
	engel_hizi//Engellerin hareket hizi
	;

	public static void main(String[] args) {//jar dosyasi olarak cikartabilmek icin main kodu
		(new TrafikBeta()).start(args);
	}
	
	boolean boyut = true;
	
	public void run() {
		if(boyut) {
			setSize(1024,768);
			boyut = false;
		}
		
		addKeyListeners();//Klavyede basilan tuslari kontrol eder.
		addMouseListeners();
		
		setBackground(Color.GRAY);
		//		tasarimi_yap();//Oyundaki objeleri tasarlar.
		ekran_boy = getHeight();
		ekran_en = getWidth();
		animasyon();//Objelerin hareketlerini duzenler
		sifirla();//Oyun bittiginde yeni oyunu hazirlamak icin tum degiskenleri eski haline dondurur.
		run();//Oyunu yeniden baslatir.
	}
	void animasyon() {
		GImage arkaPlan;
		menu_tasarimi_yap();
		try {
			arkaPlan = new GImage("images/wallpaper.jpg");
		}catch(Exception e) {
			arkaPlan=null;
			basla.setColor(Color.BLACK);
			oto_oyna.setColor(Color.BLACK);
			puan_tablosu.setColor(Color.BLACK);
			cikis.setColor(Color.BLACK);
		}

		add(arkaPlan,0,0);
		arkaPlan.sendToBack();

		while(true) {
			menudeyiz=true;
			if(animasyon_basla) {
				basla.setFont("CHILLER-80");	
			}else {
				basla.setFont("CHILLER-60");
			}
			if(animasyon_otoSur) {
				oto_oyna.setFont("CHILLER-70");
			}else {
				oto_oyna.setFont("CHILLER-50");
			}
			if(animasyon_puanTablosu) {
				puan_tablosu.setFont("CHILLER-70");
			}else {
				puan_tablosu.setFont("CHILLER-50");
			}
			if(animasyon_cikis) {
				cikis.setFont("CHILLER-70");
			}else {
				cikis.setFont("CHILLER-50");
			}
			arkaPlan.setSize(getWidth(), getHeight());
			basla.setLocation(getWidth()/2-basla.getWidth()/2,getHeight()/4);
			oto_oyna.setLocation(getWidth()/2-oto_oyna.getWidth()/2,basla.getY()+oto_oyna.getHeight());
			puan_tablosu.setLocation(getWidth()/2-puan_tablosu.getWidth()/2,oto_oyna.getY()+puan_tablosu.getHeight());
			cikis.setLocation(getWidth()/2-cikis.getWidth()/2,puan_tablosu.getY()+cikis.getHeight()*2);
			if(basla_dedi || otomatik_oyna) {
				boolean degisken = otomatik_oyna;
				sifirla();
				otomatik_oyna = degisken;
				menudeyiz = false;
				pause_kontrol = false;
				basla = null;
				oto_oyna = null;
				puan_tablosu = null;
				cikis = null;
				arkaPlan= null;
				oyun_tasarimi_yap();
				pause(10);
				break;
			}
		}
		while (true) {
			if(ekran_boy != getHeight() || ekran_en != getWidth()) {//Ekran boyutu degistiginde bu kosul gerceklesir.
				objeleri_optimize_et();//Ekran boyutu degistiginde objeleri yeni ekran boyutuna gore konumlandirir.
			}
			obje_kontrol();//Yanma ve puan kazanma durumlarini kontrol eder.
			engel_ekle();
			yol1.move(0, serit_hizi);
			yol2.move(0, serit_hizi);
			yol_kenari1.move(0, serit_hizi);
			yol_kenari2.move(0, serit_hizi);
			engel.move(0,engel_hizi);
			yol_kenari1.setSize(getWidth(),getHeight()+serit_hizi*2+5);
			yol_kenari2.setSize(getWidth(),getHeight()+serit_hizi*2+5);
			menu_don.setLocation(getWidth()-50,0);
			pause_button.setLocation(getWidth()-100,10);
			if(otomatik_oyna) {
				if(engel.getX() <= yol1.getX()+30 && engel.getY() >= 0) {
					saga_gec = true;
				}else if(engel.getX() <= yol1.getX()+YOL_EN-ENGEL_EN-20 && engel.getY() >= 0) {
					sola_gec = true;
				}
			}

			if(sola_gec && !(arac.getX() <= yol1.getX()+30)) {
				if(engel_hizi>3) {
					arac.setLocation(yol1.getX()+25,arac.getY());
				}else {
					arac.move(-engel_hizi*2.3,0);
				}

				if(arac.getX() <= yol1.getX()+30) {
					sola_gec = false;
				}
			}
			if(saga_gec && !(arac.getX()>=yol1.getX()+YOL_EN-arac.getWidth()-30)) {
				if(engel_hizi>3) {
					arac.setLocation(yol1.getX()+YOL_EN-arac.getWidth()-30,arac.getY());
				}else {
					arac.move(engel_hizi*2.3,0);
				}
				if(arac.getX() >= yol1.getX()+YOL_EN-arac.getWidth()-30) {
					saga_gec = false;
				}
			}
			if(yol1.getY() >= getHeight()) {
				yol1.setLocation(getWidth()/2-YOL_EN/2,-getHeight());	
			}else if(yol2.getY() >= getHeight()) {
				yol2.setLocation(getWidth()/2-YOL_EN/2,-getHeight());
			}
			if(yol_kenari1.getY() >= getHeight()) {
				yol_kenari1.setLocation(0,-getHeight());	
			}else if(yol_kenari2.getY() >= getHeight()) {
				yol_kenari2.setLocation(0,-getHeight());
			}
			pause(pause);//Animasyonu yavaslatmak icin bekleme suresi
			arac.sendToFront();//Araba objesini en one getirir.
			if(menudeyiz) {
				break;
			}
			if(pause_kontrol) {
				aciklama1.setLabel("Duraklatildi");
				aciklama2.setLabel("Devam etmek icin tiklayiniz");
				add(aciklama1, getWidth()/2-aciklama1.getWidth()/2, getHeight()/2-aciklama1.getHeight()*2 );
				add(aciklama2, getWidth()/2-aciklama2.getWidth()/2, getHeight()/2+aciklama1.getHeight() );
			
				waitForClick();
				aciklama2.setLabel("Menuye donmek icin tiklayiniz");
				remove(aciklama1);
				remove(aciklama2);
				pause_kontrol = false;
			}
			if(Yandimi) {//Oyuncu yandiginda bu kodlar calisir.
				aciklama1.setLabel("Oyun Bitti! "+ Integer.toString(puan) + " Puan Kazandin");
				add(aciklama1, getWidth()/2-aciklama1.getWidth()/2, getHeight()/2-aciklama1.getHeight()*2 );
				add(aciklama2, getWidth()/2-aciklama2.getWidth()/2, getHeight()/2+aciklama1.getHeight() );
				waitForClick();//Kullanicinin ekrana tiklamasini bekler.
				break;//Animasyonu bitirir.
			}
		}
	}

	void objeleri_optimize_et() {

		/*boolean sol=false,//Araba konumu solda ise true degerini alir.
				sol_engel=false;
		if(arac.getX() <= yol1.getX()+35) {//Araba konumu solda ise bu kosul gerceklesir.
			sol=true;
		}
		if(engel.getX() <= yol1.getX()+35) {//Araba konumu solda ise bu kosul gerceklesir.
			sol_engel=true;
		}*/
		yol1.setSize(YOL_EN,getHeight());
		yol2.setSize(YOL_EN,getHeight());
				otoyol_arka_plan.setSize(YOL_EN,getHeight());
		yol_kenari1.setSize(getWidth(),getHeight());
		yol_kenari2.setSize(getWidth(),getHeight());
		yol1.setLocation(getWidth()/2-YOL_EN/2,0);
		yol2.setLocation(getWidth()/2-YOL_EN/2,-getHeight());
				otoyol_arka_plan.setLocation(getWidth()/2-YOL_EN/2,0);
		yol_kenari1.setLocation(0,0);
		yol_kenari2.setLocation(0,-getHeight());
		ekran_boy = getHeight();
		ekran_en = getWidth();
		arac.setLocation(yol1.getX()+30,getHeight()-CAR_HEIGHT/2);
		engel.setLocation(yol1.getX()+YOL_EN-ENGEL_EN-25,-engel.getHeight()-50);
		/*if(sol) {
			arac.setLocation(yol1.getX()+30,getHeight()-CAR_HEIGHT/2);
		}else {
			arac.setLocation(yol1.getX()+YOL_EN-arac.getWidth()-30,getHeight()-CAR_HEIGHT);
		}
		if(sol_engel) {
			engel.setLocation(yol1.getX()+25,-engel.getHeight()-50);
		}else {
			engel.setLocation(yol1.getX()+YOL_EN-ENGEL_EN-25,-engel.getHeight()-50);
		}*/

	}

	void sifirla() {
		serit_hizi=0.6;
		engel_hizi=0.3;
		pause=1;
		puan=0;
		puan_kontrol = false;
		Yandimi = false;
		sola_gec = false;
		saga_gec = false;
		basla_dedi = false;
		otomatik_oyna = false;
		removeAll();
	}
	private void menu_tasarimi_yap() {
		basla = new GLabel("Basla");
		oto_oyna = new GLabel("Otomatik Surus");
		puan_tablosu = new GLabel("Puan Tablosu");
		cikis = new GLabel("Cikis");

		basla.setColor(Color.WHITE);
		oto_oyna.setColor(Color.WHITE);
		puan_tablosu.setColor(Color.WHITE);
		cikis.setColor(Color.WHITE);

		add(basla,getWidth()/2-basla.getWidth()/2,getHeight()/4);
		add(oto_oyna);
		add(puan_tablosu);
		add(cikis);
	}
	private void oyun_tasarimi_yap() {

		puan_metni = new GLabel("Puan: " + Integer.toString(puan));
		puan_metni.setFont("ARIAL-BOLD-30");
		puan_metni.setColor(Color.WHITE);
		add(puan_metni,5,30);
		aciklama1 = new GLabel("");
		aciklama1.setFont("ARIAL-BOLD-40");
		aciklama1.setColor(Color.ORANGE);

		aciklama2 = new GLabel("Menuye Donmek Icin Tiklayiniz...");
		aciklama2.setFont("ARIAL-BOLD-25");
		aciklama2.setColor(Color.CYAN);	

		yol_kenari1.setSize(getWidth(),getHeight()+20);
		add(yol_kenari1,0,0);
		yol_kenari2.setSize(getWidth(),getHeight()+20);
		add(yol_kenari2,0,-getHeight());

				otoyol_arka_plan = new GRect(YOL_EN,getHeight());
				otoyol_arka_plan.setFilled(true);
				otoyol_arka_plan.setFillColor(Color.DARK_GRAY);
				add(otoyol_arka_plan,getWidth()/2-YOL_EN/2,0);

		yol1.setSize(YOL_EN,getHeight());
		add(yol1,getWidth()/2-YOL_EN/2,0);
		yol2.setSize(YOL_EN,getHeight());
		add(yol2,getWidth()/2-YOL_EN/2,-getHeight()+serit_hizi*2);

		menu_don.setSize(50,50);
		add(menu_don,getWidth()-50, 0);

		pause_button.setSize(35,35);
		add(pause_button,getWidth()-100, 10);

		arac.setSize(CAR_WIDTH,CAR_HEIGHT);
		add(arac,yol1.getX()+15,getHeight()-CAR_HEIGHT/2);


		engel.setSize(ENGEL_EN, ENGEL_BOY);
		add(engel, getWidth(), getHeight()*10);

	}

	private void engel_ekle() {
		RandomGenerator rg = new RandomGenerator();
		if(((engel.getY()>-engel.getHeight() && engel.getY() < getHeight())) == false) { //Ekranda engel yok ise kosul gerceklesir
			int secim = rg.nextInt(3,11);
			try {
				engel.setImage("images/car"+ Integer.toString(secim) +".png");
			}
			catch(Exception e) {
				engel.setImage("images/car1.png");
			}
			engel.setSize(ENGEL_EN, ENGEL_BOY);
			if (rg.nextBoolean()) {//Rastgele true veya false degeri verir.///////////////////////////////
				engel.setLocation(yol1.getX()+25,-engel.getHeight());
			}
			else {
				engel.setLocation(yol1.getX()+YOL_EN-ENGEL_EN-25,-engel.getHeight());
			}
		}
	}
	private void obje_kontrol() {
		if(arac.getX() >= engel.getX() && arac.getX() <= engel.getX()+ENGEL_EN && arac.getY() <= engel.getY()+ENGEL_BOY
				|| arac.getX()+CAR_WIDTH >= engel.getX() && arac.getX()+CAR_WIDTH <= engel.getX()+ENGEL_EN && arac.getY() <= engel.getY()+ENGEL_BOY
				) {//Engelin ile arabanin konumlari cakisiyor ise bu kosul gerceklesir 
			Yandimi = true;
		}else {//Araba engele carpmadan yanindan gecmis ise
			puan_kontrol=true;
		}

		if(((engel.getY()>-engel.getHeight() && engel.getY() < getHeight())) == false && puan_kontrol){ //Ekranda engel yok ise kosul gerceklesir

			//Araba engele carpmamis ve engel ekrani terk etmis ise bu kosul gerceklesir.

			puan_metni.setLabel("Puan: " + Integer.toString(puan += engel_hizi*10));
			add(puan_metni,5,30);

			if(engel_hizi < 2) {
				engel_hizi += 0.3;
				serit_hizi += 0.3;				
			}else if(engel_hizi<4) {
				engel_hizi += 0.08;
				serit_hizi += 0.08;	
			}else if(engel_hizi<7) {
				engel_hizi += 0.05;
				serit_hizi += 0.05;	
			}else{
				engel_hizi += 0.01;
				serit_hizi += 0.01;	
			}
			puan_kontrol = false;
		}
	}
	public void keyPressed(KeyEvent e) {
		if(otomatik_oyna == false) {
			if (e.getKeyCode()== KeyEvent.VK_LEFT || e.getKeyCode()== KeyEvent.VK_A) {//Sol veya A tusuna basildiginda true olur.
				saga_gec=false;
				sola_gec=true;
				//			arac.setLocation(yol1.getX()+15, arac.getY());
			}
			else if (e.getKeyCode()== KeyEvent.VK_RIGHT || e.getKeyCode()== KeyEvent.VK_D) {//Sag veya D tusuna basildiginda true olur.
				sola_gec=false;
				saga_gec = true;
				//			arac.setLocation(yol1.getX()+YOL_EN-arac.getWidth()-15, arac.getY());
			}
		}
	}
	public void mouseMoved(MouseEvent e) {
		if(menudeyiz) {
			if(basla.getX()-10 <= e.getX() && basla.getX()+basla.getWidth()+10 >= e.getX() 
					&& basla.getY() >= e.getY() && basla.getY()-basla.getHeight()+20 <= e.getY()) {
				animasyon_basla = true;
			}else {
				animasyon_basla = false;
			}
			if(oto_oyna.getX()-10 <= e.getX() && oto_oyna.getX()+oto_oyna.getWidth()+10 >= e.getX() 
					&& oto_oyna.getY() >= e.getY() && oto_oyna.getY()-oto_oyna.getHeight()+20 <= e.getY()) {
				animasyon_otoSur = true;
			}else {
				animasyon_otoSur = false;
			}
			if(puan_tablosu.getX()-10 <= e.getX() && puan_tablosu.getX()+puan_tablosu.getWidth()+10 >= e.getX() 
					&& puan_tablosu.getY() >= e.getY() && puan_tablosu.getY()-puan_tablosu.getHeight()+20 <= e.getY()) {
				animasyon_puanTablosu = true;
			}else {
				animasyon_puanTablosu = false;
			}
			if(cikis.getX()-10 <= e.getX() && cikis.getX()+cikis.getWidth()+10 >= e.getX() 
					&& cikis.getY() >= e.getY() && cikis.getY()-cikis.getHeight()+20 <= e.getY()) {
				animasyon_cikis = true;
			}else {
				animasyon_cikis = false;
			}
		}
	}
	public void mouseClicked(MouseEvent e) {
		if(menudeyiz) {
			if(basla.getX()-10 <= e.getX() && basla.getX()+basla.getWidth()+10 >= e.getX() 
					&& basla.getY() >= e.getY() && basla.getY()-basla.getHeight()+20 <= e.getY()) {
				basla_dedi = true;
			}
			if(oto_oyna.getX()-10 <= e.getX() && oto_oyna.getX()+oto_oyna.getWidth()+10 >= e.getX() 
					&& oto_oyna.getY() >= e.getY() && oto_oyna.getY()-oto_oyna.getHeight()+20 <= e.getY()) {
				otomatik_oyna = true;
			}
			if(cikis.getX()-10 <= e.getX() && cikis.getX()+cikis.getWidth()+10 >= e.getX() 
					&& cikis.getY() >= e.getY() && cikis.getY()-cikis.getHeight()+20 <= e.getY()) {
				System.exit(0);
			}
		}else {
			if(e.getX() >= getWidth()-50 && e.getY() <= 50) {
				menudeyiz = true;
			}
			if(e.getX() >= getWidth()-100 && e.getX() <= getWidth()-50  && e.getY() <= 50) {
				pause_kontrol = true;
			}
		}

	}

}


