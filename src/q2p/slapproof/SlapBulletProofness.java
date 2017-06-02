package q2p.slapproof;

import java.util.LinkedList;
import java.util.Random;

public final class SlapBulletProofness {
	public static final Random random = new Random();
	
	public static final byte shuffles = 4;

	public static final byte[] players = new byte[] {5,6,7,11};
	public static final byte[] repeats = new byte[] {4,5,6,7,8};
	public static final byte[] ladders = new byte[] {4,5,8,9,13};
	public static final byte[] starterPick = new byte[] {5,6,7,8};
	
	public static final int tries = 10240;
	
	public static final int minWins = (int)(tries*0.75);
	
	public static final void main(final String[] args) {
		for(final byte plays : players) {
			int wons = 0;
			for(int i = tries; i != 0; i--)
				if(test((byte)(plays+1), (byte)(plays+1), plays, (byte)(plays)))
					wons++;
				
			System.out.println("["+plays+"] "+(int)(((float)wons/(float)tries)*100));
		}
	}

	private static final boolean test(final byte reps, final byte lads, final byte plays, final byte pick) {
		final LinkedList<Byte> stack = generateStack(reps, lads);
		
		shuffleStack(stack);
		
		final Player[] players = new Player[plays];
		
		for(int i = players.length-1; i != -1; i--)
			players[i] = new Player(pick, stack);
		
		final LinkedList<Byte> tower = new LinkedList<Byte>();
		
		tower.addLast(stack.removeFirst());
		
		int playerId = 0;
		
		byte activePlayers = plays;
		
		int lastPass = -1;
		
		while(true) {
			Player player = players[playerId];
			
			if(!player.out()) {
				if(lastPass == playerId) {
					return false;
				}
				
				if(!player.canFold(tower.getLast(), lads) && !stack.isEmpty())
					player.pull(stack);
				
				if(player.canFold(tower.getLast(), lads) && !stack.isEmpty()) {
					do {
						tower.addLast(player.fold(tower.getLast(), lads));
						lastPass = -1;
					} while (player.canFold(tower.getLast(), lads));
				} else {
					if(lastPass == -1)
						lastPass = playerId;
				}
				
				if(player.out()) {
					activePlayers--;
					return true;
				}
			}
			
			playerId++;
			if(playerId == plays)
				playerId = 0;
		}
	}

	private static final void shuffleStack(final LinkedList<Byte> stack) {
		for(byte times = shuffles; times != -1; times--) {
			final LinkedList<Byte> pool = new LinkedList<Byte>();
			
			while(!stack.isEmpty())
				pool.addLast(stack.remove(random.nextInt(stack.size())));
			
			while(!pool.isEmpty())
				stack.addLast(pool.removeFirst());
		}
	}

	private static LinkedList<Byte> generateStack(final byte repeats, final byte ladder) {
		final LinkedList<Byte> ret = new LinkedList<Byte>();
		
		for(byte l = ladder; l != -1; l--)
			for(byte r = repeats; r != -1; r--)
				ret.addLast(l);
		
		return ret;
	}
	
	public static final class Player {
		final LinkedList<Byte> stack = new LinkedList<Byte>();
		
		Player(byte pick, final LinkedList<Byte> stack) {
			for(; pick != 0; pick--)
				this.stack.addLast(stack.removeFirst());
		}

		public final boolean out() {
			return stack.isEmpty();
		}

		public void pull(final LinkedList<Byte> stack) {
			this.stack.addLast(stack.removeFirst());
		}

		public byte fold(final byte top, final byte ladder) {
			byte avil1 = cap(top, -1, ladder);
			byte avil2 = cap(top,  1, ladder);
						
			if(random.nextBoolean()) {
				final byte temp = avil2;				
				avil2 = avil1;
				avil1 = temp;
			}
			
			if(search(avil2))
				return fold(avil2);
				
			return fold(avil1);
		}
		
		private final byte cap(byte top, final int addition, final byte ladder) {
			top += addition;
			
			if(top == -1)
				return (byte)(ladder-1);
			if(top == ladder)
				return 0;
			return top;
		}

		private final byte fold(final byte avil) {
			stack.remove(new Byte(avil));
			return avil;
		}

		private final boolean search(final int avil) {
			for(final byte value : stack)
				if(value == avil)
					return true;
			
			return false;
		}

		public boolean canFold(final byte top, final byte ladder) {
			final byte avil1 = cap(top, -1, ladder);
			final byte avil2 = cap(top,  1, ladder);
			
			for(final byte value : stack)
				if(value == avil1 || value == avil2)
					return true;
			
			return false;
		}
	}
}