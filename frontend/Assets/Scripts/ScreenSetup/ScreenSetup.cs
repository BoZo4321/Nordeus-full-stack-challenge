using UnityEngine;

public class ScreenSetup : MonoBehaviour
{
    void Awake()
{
    Screen.SetResolution(1920, 1080, FullScreenMode.FullScreenWindow);
}
}