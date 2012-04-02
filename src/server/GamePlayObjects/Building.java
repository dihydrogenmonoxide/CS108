package server.GamePlayObjects;

import shared.game.Coordinates;

public interface Building  extends GamePlayObject{

	int getHealthPoints();

	void damage(int attackPoints);

	Coordinates getPos();

}
