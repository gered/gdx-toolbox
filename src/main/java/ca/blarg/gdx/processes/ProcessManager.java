package ca.blarg.gdx.processes;

import ca.blarg.gdx.ReflectionUtils;
import ca.blarg.gdx.Strings;
import ca.blarg.gdx.events.EventManager;
import ca.blarg.gdx.states.GameState;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.utils.Disposable;
import ca.blarg.gdx.ReflectionUtils;
import ca.blarg.gdx.Strings;
import ca.blarg.gdx.events.EventManager;
import ca.blarg.gdx.states.GameState;

import java.util.LinkedList;

public class ProcessManager implements Disposable {
	public final GameState gameState;

	LinkedList<ProcessInfo> processes;
	LinkedList<ProcessInfo> queue;

	String descriptor;

	public ProcessManager(GameState gameState) {
		if (gameState == null)
			throw new IllegalArgumentException("gameState cannot be null.");

		this.gameState = gameState;
		this.descriptor = String.format("[%s]", this.gameState.getClass().getSimpleName());

		Gdx.app.debug("ProcessManager", String.format("%s ctor", descriptor));

		processes = new LinkedList<ProcessInfo>();
		queue = new LinkedList<ProcessInfo>();
	}

	/*** public process getters ***/

	public boolean isTransitioning() {
		for (int i = 0; i < processes.size(); ++i) {
			if (processes.get(i).isTransitioning)
				return true;
		}
		return false;
	}

	public boolean isEmpty() {
		return (processes.size() == 0 && queue.size() == 0);
	}

	public boolean isProcessTransitioning(GameProcess process) {
		ProcessInfo processInfo = getProcessInfoFor(process);
		return (processInfo == null ? false : processInfo.isTransitioning);
	}

	public boolean hasProcess(String name) {
		for (int i = 0; i < processes.size(); ++i) {
			ProcessInfo info = processes.get(i);
			if (!Strings.isNullOrEmpty(info.name) && info.name.equals(name))
				return true;
		}
		return false;
	}

	/** Add / Remove ***/

	public <T extends GameProcess> T add(Class<T> processType) {
		return add(processType, null);
	}

	public <T extends GameProcess> T add(Class<T> processType, String name) {
		T newProcess;
		try {
			newProcess = ReflectionUtils.instantiateObject(processType,
			                                               new Class<?>[] { ProcessManager.class, EventManager.class },
			                                               new Object[] { this, gameState.eventManager });
		} catch (Exception e) {
			throw new IllegalArgumentException("Could not instantiate a GameProcess instance of that type.");
		}

		ProcessInfo processInfo = new ProcessInfo(newProcess, name);
		queue(processInfo);
		return newProcess;
	}

	public void remove(String name) {
		int i = getIndexOf(name);
		if (i == -1)
			throw new IllegalArgumentException("No process with that name.");
		startTransitionOut(processes.get(i), true);
	}

	public <T extends GameProcess> void removeFirstOf(Class<T> processType) {
		int i = getIndexForFirstOfType(processType);
		if (i == -1)
			throw new IllegalArgumentException("No processes of that type.");
		startTransitionOut(processes.get(i), true);
	}

	public void removeAll() {
		Gdx.app.debug("ProcessManager", String.format("%s Transitioning out all processes pending removal.", descriptor));
		for (int i = 0; i < processes.size(); ++i) {
			ProcessInfo processInfo = processes.get(i);
			if (!processInfo.isTransitioning && !processInfo.isInactive)
				startTransitionOut(processInfo, true);
		}
	}

	private void queue(ProcessInfo newProcessInfo) {
		if (newProcessInfo == null)
			throw new IllegalArgumentException("newProcessInfo cannot be null.");
		if (newProcessInfo.process == null)
			throw new IllegalArgumentException("New ProcessInfo object has null GameProcess.");

		Gdx.app.debug("ProcessManager", String.format("%s Queueing process %s.", descriptor, newProcessInfo));
		queue.add(newProcessInfo);
	}

	/*** events ***/

	public void onPause(boolean dueToOverlay) {
		if (processes.size() == 0)
			return;

		if (dueToOverlay) {
			Gdx.app.debug("ProcessManager", String.format("%s Pausing all active processes due to state being overlayed on to the parent state.", descriptor));
			for (int i = 0; i < processes.size(); ++i) {
				ProcessInfo processInfo = processes.get(i);
				if (!processInfo.isInactive) {
					Gdx.app.debug("ProcessManager", String.format("%s Pausing process %s due to parent state overlay.", descriptor, processInfo));
					processInfo.process.onPause(true);
				}
			}
		} else {
			Gdx.app.debug("ProcessManager", String.format("%s Transitioning out all active processes pending pause.", descriptor));
			for (int i = 0; i < processes.size(); ++i) {
				ProcessInfo processInfo = processes.get(i);
				if (!processInfo.isInactive)
					startTransitionOut(processInfo, false);
			}
		}
	}

	public void onResume(boolean fromOverlay) {
		if (processes.size() == 0)
			return;

		if (fromOverlay) {
			Gdx.app.debug("ProcessManager", String.format("%s Resuming all active processes due to overlay state being removed from overtop of parent state.", descriptor));
			for (int i = 0; i < processes.size(); ++i) {
				ProcessInfo processInfo = processes.get(i);
				if (!processInfo.isInactive) {
					Gdx.app.debug("ProcessManager", String.format("%s Resuming process %s due to overlay state removal.", descriptor, processInfo));
					processInfo.process.onResume(true);
				}
			}
		} else {
			Gdx.app.debug("ProcessManager", String.format("%s Resuming processes.", descriptor));
			for (int i = 0; i < processes.size(); ++i) {
				ProcessInfo processInfo = processes.get(i);
				if (processInfo.isInactive && !processInfo.isBeingRemoved) {
					Gdx.app.debug("ProcessManager", String.format("%s Resuming process %s.", descriptor, processInfo));
					processInfo.process.onResume(true);

					startTransitionIn(processInfo);
				}
			}
		}
	}

	public void onAppPause() {
		for (int i = 0; i < processes.size(); ++i) {
			ProcessInfo processInfo = processes.get(i);
			if (!processInfo.isInactive)
				processInfo.process.onAppPause();
		}
	}

	public void onAppResume() {
		for (int i = 0; i < processes.size(); ++i) {
			ProcessInfo processInfo = processes.get(i);
			if (!processInfo.isInactive)
				processInfo.process.onAppResume();
		}
	}

	public void onResize() {
		for (int i = 0; i < processes.size(); ++i) {
			ProcessInfo processInfo = processes.get(i);
			if (!processInfo.isInactive)
				processInfo.process.onResize();
		}
	}

	public void onRender(float delta) {
		for (int i = 0; i < processes.size(); ++i) {
			ProcessInfo processInfo = processes.get(i);
			if (!processInfo.isInactive)
				processInfo.process.onRender(delta);
		}
	}

	public void onUpdate(float delta) {
		cleanupInactiveProcesses();
		checkForFinishedProcesses();
		processQueue();
		updateTransitions(delta);

		for (int i = 0; i < processes.size(); ++i) {
			ProcessInfo processInfo = processes.get(i);
			if (!processInfo.isInactive)
				processInfo.process.onUpdate(delta);
		}
	}

	/*** internal process management functions ***/

	private void startTransitionIn(ProcessInfo processInfo) {
		if (processInfo == null)
			throw new IllegalArgumentException("processInfo cannot be null.");
		if (!processInfo.isInactive || processInfo.isTransitioning)
			throw new UnsupportedOperationException();

		processInfo.isInactive = false;
		processInfo.isTransitioning = true;
		processInfo.isTransitioningOut = false;
		processInfo.isTransitionStarting = true;
		Gdx.app.debug("ProcessManager", String.format("%s Transition into process %s started.", descriptor, processInfo));
	}

	private void startTransitionOut(ProcessInfo processInfo, boolean forRemoval) {
		if (processInfo == null)
			throw new IllegalArgumentException("processInfo cannot be null.");
		if (!processInfo.isInactive || processInfo.isTransitioning)
			throw new UnsupportedOperationException();

		processInfo.isTransitioning = true;
		processInfo.isTransitioningOut = true;
		processInfo.isTransitionStarting = true;
		processInfo.isBeingRemoved = forRemoval;
		Gdx.app.debug("ProcessManager", String.format("%s Transition out of process %s started pending %s.", descriptor, processInfo, (forRemoval ? "removal" : "pause")));
	}

	private void cleanupInactiveProcesses() {
		int i = 0;
		while (i < processes.size()) {
			ProcessInfo processInfo = processes.get(i);
			if (processInfo.isInactive && processInfo.isBeingRemoved) {
				// remove this process and move to the next node
				// (index doesn't change, we're removing one, so next index now equals this index)
				processes.remove(i);

				Gdx.app.debug("ProcessManager", String.format("%s Deleting inactive process %s.", descriptor, processInfo));
				processInfo.process.dispose();

			} else {
				i++;
			}
		}
	}

	private void checkForFinishedProcesses() {
		for (int i = 0; i < processes.size(); ++i) {
			ProcessInfo processInfo = processes.get(i);
			if (!processInfo.isInactive && processInfo.process.isFinished() && !processInfo.isTransitioning) {
				Gdx.app.debug("ProcessManager", String.format("%s Process %s marked as finished.", descriptor, processInfo));
				startTransitionOut(processInfo, true);
			}
		}
	}

	private void processQueue() {
		while (queue.size() > 0) {
			ProcessInfo processInfo = queue.removeFirst();

			Gdx.app.debug("ProcessManager", String.format("%s Adding process %s from queue.", descriptor, processInfo));
			processes.add(processInfo);
			processInfo.process.onAdd();

			startTransitionIn(processInfo);
		}
	}

	private void updateTransitions(float delta) {
		for (int i = 0; i < processes.size(); ++i) {
			ProcessInfo processInfo = processes.get(i);
			if (processInfo.isTransitioning) {
				boolean isDone = processInfo.process.onTransition(delta, processInfo.isTransitioningOut, processInfo.isTransitionStarting);
				if (isDone) {
					Gdx.app.debug("ProcessManager", String.format("%s Transition %s into process %s finished.",
					                                            descriptor,
					                                            (processInfo.isTransitioningOut ? "out of" : "into"),
				                                                processInfo));

					// if the process was being transitioned out, then we should mark it as
					// inactive, and trigger it's onRemove() event now
					if (processInfo.isTransitioningOut) {
						if (processInfo.isBeingRemoved) {
							Gdx.app.debug("ProcessManager", String.format("%s Removing process %s.", descriptor, processInfo));
							processInfo.process.onRemove();
						} else {
							Gdx.app.debug("ProcessManager", String.format("%s Pausing process %s.", descriptor, processInfo));
							processInfo.process.onPause(false);
						}
						processInfo.isInactive = true;
					}

					// done transitioning
					processInfo.isTransitioning = false;
					processInfo.isTransitioningOut = false;
				}
				processInfo.isTransitionStarting = false;
			}
		}
	}

	/*** private process getters ***/

	private int getIndexOf(String processName) {
		if (Strings.isNullOrEmpty(processName))
			throw new IllegalArgumentException("processName should be specified.");

		for (int i = 0; i < processes.size(); ++i) {
			ProcessInfo processInfo = processes.get(i);
			if (!Strings.isNullOrEmpty(processInfo.name) && processInfo.name.equals(processName))
				return i;
		}
		return -1;
	}

	private <T extends GameProcess> int getIndexForFirstOfType(Class<T> processType) {
		for (int i = 0; i < processes.size(); ++i) {
			if (processes.get(i).process.getClass() == processType)
				return i;
		}
		return -1;
	}

	private ProcessInfo getProcessInfoFor(GameProcess process) {
		if (process == null)
			throw new IllegalArgumentException("process cannot be null.");

		for (int i = 0; i < processes.size(); ++i) {
			if (processes.get(i).process == process)
				return processes.get(i);
		}
		return null;
	}

	/*** cleanup ***/

	public void dispose() {
		if (processes == null)
			return;

		Gdx.app.debug("ProcessManager", String.format("%s dispose", descriptor));

		while (processes.size() > 0) {
			ProcessInfo processInfo = processes.getLast();
			Gdx.app.debug("ProcessManager", String.format("%s Removing process %s as part of ProcessManager shutdown.", descriptor, processInfo));
			processInfo.process.onRemove();
			processInfo.process.dispose();
			processes.removeLast();
		}

		// the queue will likely not have anything in it, but just in case ...
		while (queue.size() > 0) {
			ProcessInfo processInfo = processes.removeFirst();
			Gdx.app.debug("ProcessManager", String.format("%s Removing queued process %s as part of ProcessManager shutdown.", descriptor, processInfo));
			processInfo.process.dispose();
		}

		processes = null;
		queue = null;
	}
}
